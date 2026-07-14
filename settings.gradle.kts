pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
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
    plugins {
        // -PbridgeVersion=… overrides (e.g. a locally-published snapshot).
        val bridgeVersion = providers.gradleProperty("bridgeVersion").orNull ?: "0.1.17"
        id("com.bitsycore.compose-desktop-native.bridge") version bridgeVersion apply false
        kotlin("multiplatform") version "2.4.0" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "2.4.0" apply false
        id("org.jetbrains.compose") version "1.12.0-beta02" apply false
        id("com.android.application") version "9.2.1" apply false
    }
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
