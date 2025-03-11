plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.hikespot.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hikespot.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // MULTI DEX ENABLE
    implementation(libs.androidx.multidex)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.runtime.livedata)
    // Lifecycles only (without ViewModel or LiveData)
    implementation(libs.androidx.lifecycle.runtime)

    // Saved state module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // Annotation processor
    annotationProcessor(libs.androidx.lifecycle.compiler)

    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    // ROOM
    implementation(libs.room.runtime)  // Room runtime
    implementation(libs.room.ktx)      // Room Kotlin extensions
    kapt(libs.room.compiler)

    // PICASSO IMAGE LIBRARY
    implementation(libs.picasso)
    implementation(libs.androidx.exifinterface)

    // CIRCLE IMAGEVIEW
    implementation(libs.circleimageview)

    // FIREBASE
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // GSON
    implementation(libs.gson)

    // RETROFIT
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}