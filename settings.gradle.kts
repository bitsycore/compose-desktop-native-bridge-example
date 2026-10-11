pluginManagement {
    repositories {
        // Opt-in, for testing a locally published compose-desktop-native (see README).
        if (providers.gradleProperty("useMavenLocal").orNull == "true") mavenLocal()
        // compose-desktop-native: the bridge plugin. Public, no credentials.
        maven("https://maven.bitsycore.com/releases") {
            content { includeGroupAndSubgroups("com.bitsycore") }
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        if (providers.gradleProperty("useMavenLocal").orNull == "true") mavenLocal()
        // compose-desktop-native: the port's klibs AND the bitsycore skiko fork
        // that the Windows (mingwX64) target renders through. macOS / Linux use
        // the official org.jetbrains.skiko from Maven Central. Public, no credentials.
        maven("https://maven.bitsycore.com/releases") {
            content { includeGroupAndSubgroups("com.bitsycore") }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "bubble-wrap"

include(":shared")
include(":androidApp")
