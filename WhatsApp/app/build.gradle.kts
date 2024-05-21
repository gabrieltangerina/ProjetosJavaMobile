plugins {
    id("com.android.application")

    // Config. Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.whatsapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.whatsapp"
        minSdk = 23
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Config. Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Config. Autenticação Firebase
    implementation("com.google.firebase:firebase-auth")

    // Config. RealtimeDatabase Firebase
    implementation("com.google.firebase:firebase-database")

    // Config. Storage Firebase
    implementation("com.google.firebase:firebase-storage")

    // Config. SmartTabLayout
    implementation("com.ogaclejapan.smarttablayout:utils-v4:2.0.0@aar")
    implementation("com.ogaclejapan.smarttablayout:library:2.0.0@aar")

    // Config. CircleImageView (Imagens circulares)
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Config. Glider (Exibir imagem salva no Firebase)
    implementation("com.github.bumptech.glide:glide:4.12.0")

}