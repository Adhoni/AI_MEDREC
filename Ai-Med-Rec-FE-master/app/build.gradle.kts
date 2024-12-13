plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
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
}

dependencies {
//    implementation("some.library:that-uses-log4j:version") {
//        exclude(group = "org.apache.logging.log4j", module = "log4j-core")
//    }

//    implementation ("com.nbsp:library:1.2")
//    implementation ("com.github.fengxiaocan:MaterialFilePicker:1.9.2")

//    implementation ("com.nbsp:library:filepicker:1.1.4")

    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.itextpdf:itext7-core:7.2.3") // Use the latest version
//    implementation("implementation 'androidx.core:core:1.7.0")0

}