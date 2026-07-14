package bubblewrap

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bubble_wrap.generated.resources.Res
import bubble_wrap.generated.resources.all_popped
import bubble_wrap.generated.resources.app_title
import bubble_wrap.generated.resources.bubble
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// ==================
// MARK: Bubble Wrap — the shared app
// ==================
// Everything below is plain Compose Multiplatform code against the OFFICIAL
// androidx.compose.* API. The same composable runs on upstream Compose
// Desktop (jvm) and on compose-desktop-native (Kotlin/Native + SDL3, no JVM)
// via the bridge plugin — see settings.gradle.kts.

@Composable
fun App() {
    var dark by remember { mutableStateOf(true) }
    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            BubbleWrap(dark = dark, onToggleDark = { dark = !dark })
        }
    }
}

@Composable
private fun BubbleWrap(dark: Boolean, onToggleDark: () -> Unit) {
    // Slider stores a size level 0..5; higher level = fewer columns = bigger
    // bubbles (each bubble fills its grid cell, see Bubble below).
    var bubbleSize by remember { mutableStateOf(3f) }
    val cols = 10 - bubbleSize.roundToInt()
    val total = cols * (84 / cols)   // ~84 bubbles, full rows only
    val rows = total / cols

    // Which bubbles are popped — reset gets a fresh sheet.
    var popped by remember { mutableStateOf(setOf<Int>()) }
    val allPopped = popped.size == total

    // safeDrawingPadding: keeps content clear of system bars under Android's
    // edge-to-edge (the Surface still paints behind them); zero on desktop.
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp)) {
        // ============
        //  Header — title, score, theme toggle, reset
        Row(verticalAlignment = Alignment.CenterVertically) {
            // composeResources drawable — decoded by the active renderer.
            Image(painterResource(Res.drawable.bubble), contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                // composeResources strings — values/strings.xml, same as anywhere.
                Text(stringResource(Res.string.app_title), style = MaterialTheme.typography.headlineMedium)
                Text(
                    if (allPopped) stringResource(Res.string.all_popped) else "${popped.size} / $total popped",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onToggleDark) { Text(if (dark) "Light" else "Dark") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { popped = emptySet() }) { Text("New sheet") }
        }

        // ============
        //  Bubble size — fewer columns = bigger, more satisfying bubbles
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Bubble size", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = bubbleSize,
                onValueChange = { bubbleSize = it; popped = emptySet() },
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier.width(220.dp).padding(start = 12.dp),
            )
        }
        Spacer(Modifier.height(8.dp))

        // ============
        //  The sheet
        Column(
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            for (row in 0 until rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until cols) {
                        val index = row * cols + col
                        Bubble(
                            popped = index in popped,
                            onPop = { popped = popped + index },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Bubble(popped: Boolean, onPop: () -> Unit, modifier: Modifier = Modifier) {
    // The pop: a bouncy spring squish. Overshoot makes it feel tactile.
    val scale by animateFloatAsState(
        targetValue = if (popped) 0.55f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
    )
    val interactions = remember { MutableInteractionSource() }
    val hovered by interactions.collectIsHoveredAsState()

    val color =
        if (popped) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.primaryContainer
    val highlight = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
    val hoverTint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f)

    // The bubble fills its weight-distributed grid cell and stays square, so
    // the column count actually drives the diameter: fewer columns = bigger.
    // Bubble, highlight dot and hover feedback are plain circles in a single
    // drawBehind — no clip layers and no ripple (the ripple's hover state
    // layer is a bounds-sized square, and per-bubble clipped layers are what
    // made the native renderer crawl). Only the pop squish keeps a layer.
    Box(
        modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .drawBehind {
                drawCircle(color)
                if (!popped) {
                    // The highlight dot that sells the "unpopped" 3D look.
                    drawCircle(
                        color = highlight,
                        radius = size.minDimension * 0.15f,
                        center = center - Offset(size.width, size.height) * 0.165f,
                    )
                    if (hovered) drawCircle(hoverTint)
                }
            }
            .clickable(interactionSource = interactions, indication = null, enabled = !popped, onClick = onPop),
        contentAlignment = Alignment.Center,
    ) {
        if (popped) {
            Text("pop", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}
