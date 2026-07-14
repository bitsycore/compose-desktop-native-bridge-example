import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Plain Android application module — nothing multiplatform in here.
// It just consumes the shared CMP code from :shared and adds the Activity.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.bitsycore.bubblewrap"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.bitsycore.bubblewrap"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // The shared Compose Multiplatform UI — androidx.compose.* on this target.
    implementation(project(":shared"))
    // Android entry point
    implementation("androidx.activity:activity-compose:1.13.0")
}
