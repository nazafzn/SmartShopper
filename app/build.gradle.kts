plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.smartshopper"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.smartshopper"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    //should probably clean this up
    implementation(libs.litert)
    implementation(libs.litert.support.api)
    val room_version = "2.8.4"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)

    implementation("androidx.activity:activity:1.9.3")

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.common.jvm)

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation(libs.junit)
}
