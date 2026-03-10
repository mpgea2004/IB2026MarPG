plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.iberdrola.practicas2026.MarPG"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.iberdrola.practicas2026.MarPG"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Hilt

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler) // <--- Cambiado a ksp
    // Room (Para el caché offline)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // Red: Retrofit + Gson + Retromock
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retromock) // Asegúrate de tenerlo en el libs.versions.toml

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}