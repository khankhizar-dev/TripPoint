plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.apollo)
}

android {
    namespace = "com.android.trippoint.core.network"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.apollo.runtime)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi.kotlin)
    testImplementation(libs.junit)
    testImplementation(libs.mockwebserver)
}
