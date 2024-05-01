plugins {
    id("com.android.application")

    // Config. Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.organizze"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.organizze"
        minSdk = 21
        targetSdk = 34
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

    // Config. Slider
    dataBinding{
        enable = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Config. Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")

    // Config. RealtimeDatabase
    implementation("com.google.firebase:firebase-database")

    // Config. Authentication
    implementation("com.google.firebase:firebase-auth")

    // Config. Slider
    implementation("com.heinrichreimersoftware:material-intro:2.0.0")
}