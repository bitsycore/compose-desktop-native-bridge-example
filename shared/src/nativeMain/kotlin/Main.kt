package bubblewrap

import com.compose.sdl.AppWindowIcon
import com.compose.sdl.Window
import com.compose.sdl.nativeComposeApp

// Native entry point - compose-desktop-native's SDL3 window shell, shaped like
// Compose Desktop's application { Window { } } (compare MainJvm.kt). No JVM:
// this compiles to a self-contained native executable.
fun main() = nativeComposeApp {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bubble Wrap",
        width = 560,
        height = 760,
        // The bridge's icon { } packaging (shared/build.gradle.kts).
        icon = AppWindowIcon(light = listOf("icon/bubble.rgba")),
    ) {
        App()
    }
}
