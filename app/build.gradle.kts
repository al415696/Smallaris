plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    alias(libs.plugins.compose.compiler)

}

android {
    namespace = "es.uji.smallaris"
    compileSdk = 35

    defaultConfig {
        applicationId = "es.uji.smallaris"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        val apiKey: String = project.findProperty("OPENROUTESERVICE_API_KEY") as String
        buildConfigField("String", "OPENROUTESERVICE_API_KEY", "\"$apiKey\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE.md",
                    "META-INF/NOTICE.txt",
                    "/META-INF/AL2.0",
                    "META-INF/LICENSE-notice.md",  // Nueva exclusión
                    "/META-INF/LGPL2.1"
                )
            )
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    Material
    implementation(libs.material.icons.extended)
//    implementation ("androidx.compose.material:material-icons-extended:2.8.7")


//    lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:7.3.1")

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation(kotlin("script-runtime"))

//    androidTestImplementation("org.mockito:mockito-core:5.0.0")
//    androidTestImplementation("org.mockito:mockito-android:5.0.0")
//    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.13")

    implementation("com.mapbox.maps:android:11.7.0")
    implementation("com.mapbox.extension:maps-compose:11.7.0")

}