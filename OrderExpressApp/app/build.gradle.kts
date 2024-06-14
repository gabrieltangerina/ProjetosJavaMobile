plugins {
    alias(libs.plugins.androidApplication)

    // Config. Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.layoutideia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.layoutideia"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Config. Temas
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Config. Barra de pesquisa
    implementation("com.github.mancj:MaterialSearchBar:0.8.5")

    // Config. SmartTabLayout
    implementation("com.ogaclejapan.smarttablayout:utils-v4:2.0.0@aar")
    implementation("com.ogaclejapan.smarttablayout:library:2.0.0@aar")

    // Config. Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Config. RealtimeDatabase Firebase
    implementation("com.google.firebase:firebase-database")

    // Config. Autenticação Firebase
    implementation("com.google.firebase:firebase-auth")
}