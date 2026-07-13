import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

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
    var columns by remember { mutableStateOf(7f) }
    val cols = columns.roundToInt()
    val total = cols * (84 / cols)   // ~84 bubbles, full rows only
    val rows = total / cols

    // Which bubbles are popped — reset gets a fresh sheet.
    var popped by remember { mutableStateOf(setOf<Int>()) }
    val allPopped = popped.size == total

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // ============
        //  Header — title, score, theme toggle, reset
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Bubble Wrap", style = MaterialTheme.typography.headlineMedium)
                Text(
                    if (allPopped) "All popped — very satisfying." else "${popped.size} / $total popped",
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
                value = columns,
                onValueChange = { columns = it; popped = emptySet() },
                valueRange = 5f..10f,
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
    val color =
        if (popped) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.primaryContainer

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .padding(2.dp)
                .size(44.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(CircleShape)
                .background(color)
                .clickable(enabled = !popped, onClick = onPop),
            contentAlignment = Alignment.Center,
        ) {
            // A subtle highlight dot sells the "unpopped" 3D look.
            if (!popped) {
                Box(
                    Modifier
                        .size(14.dp)
                        .graphicsLayer { translationX = -8f; translationY = -8f }
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f))
                )
            } else {
                Text("pop", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            }
        }
    }
}
