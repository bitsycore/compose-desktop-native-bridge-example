// Nothing builds at the root anymore — :shared holds the multiplatform
// library (including the native desktop executables) and :androidApp the
// Android application. Versions are pinned in settings.gradle.kts; applying
// everything here with `apply false` keeps each plugin in a single
// classloader shared by both modules.
plugins {
    kotlin("multiplatform") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("org.jetbrains.kotlin.plugin.compose") apply false
    id("org.jetbrains.compose") apply false
    id("com.android.application") apply false
    id("com.android.kotlin.multiplatform.library") apply false
    id("com.bitsycore.compose-desktop-native.bridge") apply false
}
