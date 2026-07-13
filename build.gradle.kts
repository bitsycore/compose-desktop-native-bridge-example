import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "2.4.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
    id("org.jetbrains.compose") version "1.12.0-beta01"
}

// The port's version for its OWN artifacts (:window — the SDL shell has no
// upstream equivalent). The bridge plugin substitutes the SAME version for
// the official coordinates below; both default to the plugin's version.
val cdnVersion = providers.gradleProperty("composeDesktopNative.version").orNull ?: "0.1.14"

kotlin {
    jvm()

    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

    targets.withType<KotlinNativeTarget>().all {
        val isMingw = name == "mingwX64"
        val isLinux = name.startsWith("linux")
        binaries.executable {
            entryPoint = "main"
            // SDL3 + FreeType (+ SDL3_ttf / SDL3_image on Windows) ship as
            // static archives INSIDE the port's cinterop klibs — nothing to
            // install, no runtime DLLs. Only shell-level flags live here.
            if (isMingw) linkerOpts(
                "-Wl,--gc-sections", "-Wl,-s",
                "-Wl,--subsystem,windows", "-Wl,-e,mainCRTStartup",
            )
            // Skia (the default renderer on Linux) references the system
            // graphics stack.
            if (isLinux) linkerOpts(
                "-L/usr/lib/x86_64-linux-gnu", "-L/usr/lib/aarch64-linux-gnu",
                "-lfontconfig", "-lGL", "-lX11",
            )
        }
    }

    sourceSets {
        commonMain.dependencies {
            // OFFICIAL Compose Multiplatform coordinates — every artifact the
            // shared code touches, declared directly. The bridge plugin swaps
            // them for compose-desktop-native's klibs on the native targets;
            // jvm (and android/ios/wasm, if added) resolve them from Maven.
            implementation("org.jetbrains.compose.runtime:runtime:1.11.1")
            implementation("org.jetbrains.compose.ui:ui:1.12.0-beta01")
            implementation("org.jetbrains.compose.ui:ui-graphics:1.12.0-beta01")
            implementation("org.jetbrains.compose.ui:ui-unit:1.12.0-beta01")
            implementation("org.jetbrains.compose.foundation:foundation:1.12.0-beta01")
            implementation("org.jetbrains.compose.foundation:foundation-layout:1.12.0-beta01")
            implementation("org.jetbrains.compose.animation:animation-core:1.12.0-beta01")
            implementation("org.jetbrains.compose.material3:material3:1.12.0-alpha03")
        }
        nativeMain.dependencies {
            // The port's window shell + SDL3 main loop (its own API, not a
            // substituted upstream coordinate).
            implementation("com.bitsycore.compose.sdl:window:$cdnVersion")
        }
        jvmMain.dependencies {
            // Upstream Compose Desktop for the jvm window shell.
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainJvmKt"
    }
}
