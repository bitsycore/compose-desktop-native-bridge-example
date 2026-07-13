import com.compose.sdl.nativeComposeWindow

// Native entry point — compose-desktop-native's SDL3 window shell. No JVM:
// this compiles to a self-contained native executable.
fun main() {
    nativeComposeWindow(title = "Bubble Wrap", width = 560, height = 760) { App() }
}
