plugins {
    kotlin("multiplatform") version "2.4.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
    id("org.jetbrains.compose") version "1.12.0-beta01"
    id("com.android.application") version "9.2.1"
    id("com.bitsycore.compose-desktop-native.bridge")
}

// Compose Desktop Native own artifact version
val cdnVersion = providers.gradleProperty("composeDesktopNative.version").orNull ?: "0.1.17"

kotlin {
    jvm()
    androidTarget()

    // Compose Desktop Native
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            // Plugin redirect properly CMP artifacts
            implementation("org.jetbrains.compose.runtime:runtime:1.11.1")
            implementation("org.jetbrains.compose.ui:ui:1.12.0-beta01")
            implementation("org.jetbrains.compose.ui:ui-graphics:1.12.0-beta01")
            implementation("org.jetbrains.compose.ui:ui-text:1.12.0-beta01")
            implementation("org.jetbrains.compose.ui:ui-unit:1.12.0-beta01")
            implementation("org.jetbrains.compose.foundation:foundation:1.12.0-beta01")
            implementation("org.jetbrains.compose.foundation:foundation-layout:1.12.0-beta01")
            implementation("org.jetbrains.compose.animation:animation-core:1.12.0-beta01")
            implementation("org.jetbrains.compose.material3:material3:1.12.0-alpha03")
            implementation("org.jetbrains.compose.components:components-resources:1.12.0-beta01")
        }
        nativeMain.dependencies {
            // Compose Desktop Native entry point
            implementation("com.bitsycore.compose.sdl:window:$cdnVersion")
        }
        jvmMain.dependencies {
            // Compose Desktop entry point
            implementation(compose.desktop.currentOs)
        }
        androidMain.dependencies {
            // Android entry point
            implementation("androidx.activity:activity-compose:1.13.0")
        }
    }
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

android {
    namespace = "com.bitsycore.bubblewrap"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.bitsycore.bubblewrap"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
