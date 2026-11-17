plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.desktop.native.bridge)
}

kotlin {
    jvm()

    android {
        namespace = "com.bitsycore.bubblewrap.shared"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }

    // Compose Desktop Native
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

    // The bridge plugin reports the exact Compose versions the port tracks, so
    // they are never hand-matched against the release (material3 is versioned
    // separately upstream - an easy mismatch this removes). `.version` is the
    // port klib version being substituted.
    val cmp = composeDesktopNative.compose
    val cmpMaterial3 = composeDesktopNative.composeMaterial3
    val cmpRuntime = composeDesktopNative.composeRuntime
    val cdn = composeDesktopNative.version

    sourceSets {
        commonMain.dependencies {
            // Official CMP coords: the bridge substitutes these for the port's
            // klibs on the native desktop targets; android/jvm resolve them as-is.
            implementation("org.jetbrains.compose.runtime:runtime:$cmpRuntime")
            implementation("org.jetbrains.compose.ui:ui:$cmp")
            implementation("org.jetbrains.compose.ui:ui-geometry:$cmp")
            implementation("org.jetbrains.compose.ui:ui-graphics:$cmp")
            implementation("org.jetbrains.compose.ui:ui-text:$cmp")
            implementation("org.jetbrains.compose.ui:ui-unit:$cmp")
            implementation("org.jetbrains.compose.foundation:foundation:$cmp")
            implementation("org.jetbrains.compose.foundation:foundation-layout:$cmp")
            implementation("org.jetbrains.compose.animation:animation-core:$cmp")
            implementation("org.jetbrains.compose.components:components-resources:$cmp")
            implementation("org.jetbrains.compose.material3:material3:$cmpMaterial3")
        }
        nativeMain.dependencies {
            // The port's own windowing / main-loop API (not substituted); tracks
            // the port version the bridge substitutes.
            implementation("com.bitsycore.compose:desktop-native-window:$cdn")
        }
        jvmMain.dependencies {
            // Compose Desktop entry point
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    // Keep the Res package from the single-module days so the
    // bubble_wrap.generated.resources imports stay stable.
    packageOfResClass = "bubble_wrap.generated.resources"
}

compose.desktop {
    // Upstream Compose Desktop (jvm) entry point.
    application {
        mainClass = "bubblewrap.MainJvmKt"
    }
    // compose-desktop-native: declares an executable with this entry point on
    // every native desktop target, and packages data.kres next to it.
    native {
        entryPoint = "bubblewrap.main"
        // Window / taskbar icon (data.kres icon/bubble.rgba, picked up by
        // Window(icon = AppWindowIcon(...)) in Main.kt) and, on Windows, the icon
        // embedded in the .exe - no extra tools needed.
        icon {
            light.from("src/commonMain/composeResources/drawable/bubble.png")
        }
        // Noto Sans (+ Italic) and Noto Sans Mono, the port's default fonts, are
        // bundled into data.kres by default - `fonts { }` only opts out.
    }
}
