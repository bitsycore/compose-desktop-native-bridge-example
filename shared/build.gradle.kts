plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.kotlin.multiplatform.library")
    id("com.bitsycore.compose-desktop-native.bridge")
}

// Compose Desktop Native own artifact version
val cdnVersion = providers.gradleProperty("composeDesktopNative.version").get()

kotlin {
    jvm()

    // Android via AGP's KMP library plugin — the android target is configured
    // right here, no android {} block and no AndroidManifest.xml needed.
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
            implementation("org.jetbrains.compose.runtime:runtime:1.12.0-beta02")
            implementation("org.jetbrains.compose.ui:ui:1.12.0-beta02")
            implementation("org.jetbrains.compose.ui:ui-graphics:1.12.0-beta02")
            implementation("org.jetbrains.compose.ui:ui-text:1.12.0-beta02")
            implementation("org.jetbrains.compose.ui:ui-unit:1.12.0-beta02")
            implementation("org.jetbrains.compose.foundation:foundation:1.12.0-beta02")
            implementation("org.jetbrains.compose.foundation:foundation-layout:1.12.0-beta02")
            implementation("org.jetbrains.compose.animation:animation-core:1.12.0-beta02")
            implementation("org.jetbrains.compose.material3:material3:1.12.0-alpha03")
            implementation("org.jetbrains.compose.components:components-resources:1.12.0-beta02")
        }
        nativeMain.dependencies {
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
