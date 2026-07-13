// Bubble Wrap — a tiny all-platform Compose Multiplatform app built on
// compose-desktop-native through its BRIDGE plugin: the app declares only the
// OFFICIAL org.jetbrains.compose coordinates; on the Kotlin/Native desktop
// targets (mingwX64 / linuxX64 / linuxArm64 / macosArm64) the plugin
// substitutes the com.bitsycore.compose.sdl klibs, while the jvm target keeps
// resolving upstream Compose Desktop untouched.

pluginManagement {
    repositories {
        mavenLocal()          // local snapshots of the port (development)
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // The bridge plugin is published to GitHub Packages — auth required
        // even for public packages: set gpr.user / gpr.token in
        // ~/.gradle/gradle.properties (a classic PAT with read:packages), or
        // export GITHUB_ACTOR / GITHUB_TOKEN.
        maven {
            name = "ComposeDesktopNative"
            url = uri("https://maven.pkg.github.com/bitsycore/compose-desktop-native")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                    ?: providers.gradleProperty("gpr.user").orNull ?: ""
                password = System.getenv("GITHUB_TOKEN")
                    ?: providers.gradleProperty("gpr.token").orNull ?: ""
            }
        }
    }
    plugins {
        // -PbridgeVersion=… overrides (e.g. a locally-published snapshot).
        id("com.bitsycore.compose-desktop-native.bridge") version
            (providers.gradleProperty("bridgeVersion").orNull ?: "0.1.14")
    }
}

plugins {
    id("com.bitsycore.compose-desktop-native.bridge")
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven {
            name = "ComposeDesktopNative"
            url = uri("https://maven.pkg.github.com/bitsycore/compose-desktop-native")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                    ?: providers.gradleProperty("gpr.user").orNull ?: ""
                password = System.getenv("GITHUB_TOKEN")
                    ?: providers.gradleProperty("gpr.token").orNull ?: ""
            }
        }
    }
}

rootProject.name = "bubble-wrap"
