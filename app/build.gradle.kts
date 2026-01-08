plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt") // <-- required for Hilt
    id("com.google.dagger.hilt.android") // <-- Hilt plugin
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.assignment1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.assignment1"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures{
        viewBinding = true

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.lifecycle.essentials)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.androidx.recyclerview)
    implementation(libs.work.runtime.ktx)
    implementation(libs.androidx.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Retrofit (using the bundle we created)
    implementation(libs.bundles.retrofit.stack)

    // Room
    implementation(libs.bundles.room.stack)
    ksp(libs.androidx.room.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    // In build.gradle.kts
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation(libs.androidx.work.runtime.ktx)
}
kapt {
    correctErrorTypes = true
}