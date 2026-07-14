plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.kotlin.multiplatform.library")
    id("com.bitsycore.compose-desktop-native.bridge")
}

kotlin {
    jvm()

    android {
        namespace = "com.bitsycore.bubblewrap.shared"
        compileSdk = 37
        minSdk = 24
    }

    // Compose Desktop Native
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            // Plugin redirect properly CMP artifacts
            val version = providers.gradleProperty("composeMultiplatformVersion").get()
            implementation("org.jetbrains.compose.runtime:runtime:$version")
            implementation("org.jetbrains.compose.ui:ui:$version")
            implementation("org.jetbrains.compose.ui:ui-geometry:$version")
            implementation("org.jetbrains.compose.ui:ui-graphics:$version")
            implementation("org.jetbrains.compose.ui:ui-text:$version")
            implementation("org.jetbrains.compose.ui:ui-unit:$version")
            implementation("org.jetbrains.compose.foundation:foundation:$version")
            implementation("org.jetbrains.compose.foundation:foundation-layout:$version")
            implementation("org.jetbrains.compose.animation:animation-core:$version")
            implementation("org.jetbrains.compose.components:components-resources:$version")
            val m3Version = providers.gradleProperty("material3ComposeVersion").get()
            implementation("org.jetbrains.compose.material3:material3:$m3Version")
        }
        nativeMain.dependencies {
            // Compose Desktop Native own artifact version
            val cdnVersion = providers.gradleProperty("composeDesktopNativeVersion").get()
            // Compose Desktop Native entry point
            implementation("com.bitsycore.compose.sdl:window:$cdnVersion")
        }
        jvmMain.dependencies {
            // Compose Desktop entry point
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    // Keep the Res package from the single-module days so App.kt's
    // bubble_wrap.generated.resources imports stay stable.
    packageOfResClass = "bubble_wrap.generated.resources"
}

compose.desktop {
    // Upstream Compose Desktop (jvm) entry point.
    application {
        mainClass = "bubblewrap.MainJvmKt"
    }
    // compose-desktop-native entry point — the bridge plugin declares an
    // executable with this entry point on every native desktop target.
    native {
        entryPoint = "bubblewrap.main"
    }
}
