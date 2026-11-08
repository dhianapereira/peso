import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val releaseVersionName = providers.environmentVariable("RELEASE_VERSION_NAME")
val releaseVersionCode = providers.environmentVariable("RELEASE_VERSION_CODE")
val releaseKeystorePath = providers.environmentVariable("RELEASE_KEYSTORE_PATH")
val releaseKeystorePassword = providers.environmentVariable("RELEASE_KEYSTORE_PASSWORD")
val releaseKeyAlias = providers.environmentVariable("RELEASE_KEY_ALIAS")
val releaseKeyPassword = providers.environmentVariable("RELEASE_KEY_PASSWORD")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "io.github.dhianapereira.peso"
    compileSdk = 37
    defaultConfig {
        applicationId = "io.github.dhianapereira.peso"
        minSdk = 26
        targetSdk = 35
        versionCode = releaseVersionCode.orNull?.toInt() ?: 1
        versionName = releaseVersionName.orNull ?: "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ksp { arg("room.schemaLocation", "$projectDir/schemas") }
    }
    signingConfigs {
        if (
            releaseKeystorePath.isPresent &&
            releaseKeystorePassword.isPresent &&
            releaseKeyAlias.isPresent &&
            releaseKeyPassword.isPresent
        ) {
            create("release") {
                storeFile = file(releaseKeystorePath.get())
                storePassword = releaseKeystorePassword.get()
                keyAlias = releaseKeyAlias.get()
                keyPassword = releaseKeyPassword.get()
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfigs.findByName("release")?.let { signingConfig = it }
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures { compose = true }
    lint { disable += setOf("AndroidGradlePluginVersion", "GradleDependency", "NewerVersionAvailable", "OldTargetApi") }
    androidResources {
        generateLocaleConfig = true
        localeFilters += listOf("pt", "en")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

ktlint {
    android.set(true)
    outputToConsole.set(true)
    filter { exclude { it.file.path.contains("/generated/") } }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.3.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.room:room-ktx:2.8.5")
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("com.google.dagger:hilt-android:2.60.1")
    ksp("com.google.dagger:hilt-android-compiler:2.60.1")
    ksp("androidx.room:room-compiler:2.8.5")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
