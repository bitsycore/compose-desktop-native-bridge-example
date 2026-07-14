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
        // -PbridgeVersion=… overrides (e.g. locally-published snapshot).
        val bridgeVersion = providers.gradleProperty("bridgeVersion").get()
        val kotlinVersion = "2.4.10"
        val agpVersion = "9.2.1"
        val cmpVersion = "1.12.0-beta02"

        kotlin("multiplatform") version kotlinVersion apply false
        id("org.jetbrains.kotlin.android") version kotlinVersion apply false
        id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion apply false


        id("com.bitsycore.compose-desktop-native.bridge") version bridgeVersion apply false

        id("org.jetbrains.compose") version cmpVersion apply false

        id("com.android.application") version agpVersion apply false
        id("com.android.kotlin.multiplatform.library") version agpVersion apply false
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

include(":shared")
include(":androidApp")
