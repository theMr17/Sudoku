plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf") version("0.9.1")
}

android {
    namespace = "com.mr_17.sudoku"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mr_17.sudoku"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // proto datastore
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("com.google.protobuf:protobuf-javalite:3.14.0")

    // jetpack compose
    implementation("androidx.compose.ui:ui-graphics-android:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.10.0"
    }

    generateProtoTasks {
        all().forEach  { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
