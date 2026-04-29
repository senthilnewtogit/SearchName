plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

kotlin {
    jvmToolchain(21)
}

android {
    namespace = "com.cvs.aetna.wiring"
    compileSdk = 36
}

dependencies {
    implementation(project(":app:search:public"))
    implementation(project(":app:search:private"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}