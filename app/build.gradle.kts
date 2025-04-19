plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.login"
    compileSdk = 35 // usa al menos 34 (android 14)

    defaultConfig {
        applicationId = "com.example.login"
        minSdk = 26 // permite que funcione en dispositivos antiguos
        targetSdk = 34 // compatible con google play y versiones nuevas
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}