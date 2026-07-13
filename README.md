# Bubble Wrap 🫧

A tiny, deliberately silly **Material 3 bubble-wrap simulator** — and a
complete consumer example for
[compose-desktop-native](https://github.com/bitsycore/compose-desktop-native)'s
**bridge plugin**: one shared Compose Multiplatform codebase that runs

- on **upstream Compose Desktop** (JVM), and
- as **self-contained native executables** — Windows (mingwX64), Linux
  (x64 / arm64) and macOS (arm64) — **no JVM**, rendering through SDL3/Skia.

Pop bubbles, resize them with a slider, flip dark/light, get a fresh sheet.
Bouncy spring physics included.

## The point

`src/commonMain` contains only plain Compose code against the **official**
`androidx.compose.*` API, and `build.gradle.kts` declares only the **official**
`org.jetbrains.compose.*` coordinates. One line in `settings.gradle.kts` does
the rest:

```kotlin
plugins { id("com.bitsycore.compose-desktop-native.bridge") version "0.1.14" }
```

On the Kotlin/Native desktop targets the plugin substitutes
compose-desktop-native's klibs for those coordinates; every other target keeps
resolving upstream. SDL3 and its codecs ship as static archives *inside* the
klibs — nothing to install, and a distributable is just the executable.

## Setup

The artifacts live on GitHub Packages, which requires authentication even for
public downloads. Put a classic PAT with `read:packages` in
`~/.gradle/gradle.properties`:

```properties
gpr.user=<your-github-username>
gpr.token=<your-pat>
```

## Run

```bash
# Native — self-contained executable, no JVM
./gradlew runDebugExecutableMingwX64      # Windows
./gradlew runDebugExecutableLinuxX64      # Linux
./gradlew runDebugExecutableMacosArm64    # macOS (Apple Silicon)

# Upstream Compose Desktop (JVM) — the same App()
./gradlew run
```

Linux needs the usual GL/X11/fontconfig dev basics at link time
(`libfontconfig-dev libgl1-mesa-dev libx11-dev`).

## Developing against a local build of the port

Publish snapshots from a compose-desktop-native checkout
(`publishMingwX64PublicationToMavenLocal publishKotlinMultiplatformPublicationToMavenLocal
:compose-desktop-native-bridge:publishToMavenLocal`), then:

```bash
./gradlew runDebugExecutableMingwX64 \
  -PbridgeVersion=0.0.0-SNAPSHOT \
  -PcomposeDesktopNative.version=0.0.0-SNAPSHOT
```

## License

[MIT](LICENSE.md)
