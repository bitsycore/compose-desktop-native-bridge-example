# Bubble Wrap 🫧

A tiny, deliberately silly **Material 3 bubble-wrap simulator** - and a
complete consumer example for
[compose-desktop-native](https://github.com/bitsycore/compose-desktop-native)'s
**bridge plugin**: one shared Compose Multiplatform codebase that runs

- on **Android** (Google's androidx.compose artifacts),
- on **upstream Compose Desktop** (JVM), and
- as **self-contained native executables** - Windows (mingwX64), Linux
  (x64 / arm64) and macOS (arm64) - **no JVM**, rendering through SDL3/Skia.

Pop bubbles, resize them with a slider, flip dark/light, get a fresh sheet.
Bouncy spring physics included. The bubble logo and the strings come from
**`composeResources/`** - same files, same generated `Res.*` accessors on
every platform; on the native targets the bridge bundles them into
`data.kres` next to the executable automatically.

## Layout

Two modules, the current Android-recommended shape for a KMP app. Versions
live in a single version catalog, `gradle/libs.versions.toml`.

- **`:shared`** - the whole app as a Compose Multiplatform library. The
  Android target comes from AGP's KMP library plugin
  (`com.android.kotlin.multiplatform.library`), declared as `android {}`
  right inside the `kotlin {}` block - no top-level `android {}` block, no
  manifest. The jvm/native desktop entry points and the bridge plugin live
  here too.
- **`:androidApp`** - a plain `com.android.application` module: one
  Activity, one manifest, `implementation(project(":shared"))`.

## The point

`shared/src/commonMain` contains only plain Compose code against the
**official** `androidx.compose.*` API, and `shared/build.gradle.kts` declares
only the **official** `org.jetbrains.compose.*` coordinates. Applying the
bridge plugin does the rest:

```kotlin
// shared/build.gradle.kts - version in gradle/libs.versions.toml
plugins { alias(libs.plugins.compose.desktop.native.bridge) }
```

On the Kotlin/Native desktop targets the plugin substitutes
compose-desktop-native's klibs for those coordinates; every other target keeps
resolving upstream - on **Android**, JetBrains' own artifacts already redirect
to Google's `androidx.compose.*`, the exact same trick one layer up. The plugin
also reports the exact Compose versions the port tracks
(`composeDesktopNative.compose` / `.composeMaterial3` / `.composeRuntime`), so
the Compose library versions are never hand-matched.

SDL3 and its codecs ship as static archives *inside* the klibs - nothing to
install. A distributable is the executable plus its `data.kres` resource
bundle (on Windows also `skiko-windows-x64.dll` + `icudtl.dat`, which the
plugin drops next to the `.exe`).

`compose.desktop.native { }` in `shared/build.gradle.kts` is the native
counterpart of `compose.desktop.application { }`:

- `entryPoint` declares the executable on every native desktop target;
- `icon { }` bundles the window / taskbar icon (the same `bubble.png` the app
  shows), selected in `Main.kt` with `AppWindowIcon`;
- the port's default fonts (Noto Sans, Noto Sans Italic, Noto Sans Mono) are
  bundled automatically, so text looks the same on every OS - `fonts { }`
  only opts out.

The two desktop entry points mirror each other: `Main.kt` is
`nativeComposeApp { Window(...) }`, `MainJvm.kt` is Compose Desktop's
`application { Window(...) }`.

## Setup

Nothing to set up. The bridge plugin, the port's klibs and the Windows skiko
fork all come from the public `https://maven.bitsycore.com/releases`
repository (see `settings.gradle.kts`) - no account, no token.

| Toolchain | Version |
|---|---|
| compose-desktop-native (bridge plugin + klibs) | 1.12.1-2 |
| Compose Multiplatform | 1.12.1 |
| Kotlin | 2.4.21 |
| Android Gradle Plugin | 9.4.1 |
| Gradle | 9.8.1 |

## Run

```bash
# Native - self-contained executable, no JVM
./gradlew :shared:runDebugExecutableMingwX64      # Windows
./gradlew :shared:runDebugExecutableLinuxX64      # Linux
./gradlew :shared:runDebugExecutableMacosArm64    # macOS (Apple Silicon)

# Upstream Compose Desktop (JVM) - the same App()
./gradlew :shared:run

# Android - the same App() on androidx.compose
./gradlew :androidApp:installDebug        # or open in Android Studio
```

Each host builds the native targets it can link: mingwX64 on Windows,
macosArm64 on macOS, linuxX64 / linuxArm64 on Linux. Linux needs the usual
GL/X11/fontconfig dev basics at link time
(`libfontconfig-dev libgl1-mesa-dev libx11-dev`).

## Developing against a local build of the port

Publish snapshots from a compose-desktop-native checkout
(`publishMingwX64PublicationToMavenLocal publishKotlinMultiplatformPublicationToMavenLocal
:compose-desktop-native-bridge:publishToMavenLocal`), set `composeDesktopNative`
in `gradle/libs.versions.toml` to `0.0.0-SNAPSHOT`, then:

```bash
./gradlew :shared:runDebugExecutableMingwX64 -PuseMavenLocal=true
```

`-PcomposeDesktopNative.version=...` substitutes a different port klib version
than the bridge plugin's own, if you need to mix releases.

## License

[MIT](LICENSE.md)
