plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.google.firebase.database)
    implementation(libs.firebase.ui.database)
    implementation(platform(libs.firebase.bom))
    androidTestImplementation (libs.espresso.core)
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    testImplementation ("org.mockito:mockito-core:4.0.0")
    testImplementation ("androidx.test:core:1.4.0")
    testImplementation ("org.robolectric:robolectric:4.7.3")
    testImplementation ("androidx.test:core:1.4.0")
    testImplementation ("org.easymock:easymock:4.3")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation ("org.easymock:easymock:4.3")


}
