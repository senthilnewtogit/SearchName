plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)
}

android {
    namespace = "com.cvs.aetna.search.data"
    compileSdk = 36
    defaultConfig {
        minSdk = 28
    }
}

dependencies {

    implementation(project(":app:search:domain"))
    implementation(project(":app:search:public"))

    implementation(libs.androidx.annotation.experimental)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

}