plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.hanif.ar_poc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hanif.ar_poc"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    with(libs) {
        with(androidx) {
            implementation(core.ktx)
            implementation(lifecycle.runtime.ktx)
            implementation(activity.compose)
            implementation(platform(compose.bom))
            androidTestImplementation(platform(compose.bom))
            implementation(ui)
            implementation(material3)
            androidTestImplementation(junit)
            androidTestImplementation(espresso.core)
            implementation(ui.graphics)
            implementation(ui.tooling.preview)
            androidTestImplementation(ui.test.junit4)
            debugImplementation(ui.tooling)
            debugImplementation(ui.test.manifest)
        }


        testImplementation(junit)

        // ARSceneView
        implementation(arsceneview)

        //Navigation
        implementation("androidx.navigation:navigation-compose:2.7.7")
    }

}