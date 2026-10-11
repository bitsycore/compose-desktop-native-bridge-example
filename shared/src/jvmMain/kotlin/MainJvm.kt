package bubblewrap

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import bubble_wrap.generated.resources.Res
import bubble_wrap.generated.resources.bubble
import org.jetbrains.compose.resources.painterResource

// JVM entry point - the SAME App() on upstream Compose Desktop
// (`./gradlew :shared:run`), untouched by the bridge.
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bubble Wrap - JVM (upstream Compose)",
        state = rememberWindowState(width = 560.dp, height = 760.dp),
        icon = painterResource(Res.drawable.bubble),
    ) {
        App()
    }
}
