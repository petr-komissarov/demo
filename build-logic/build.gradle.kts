plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.jvm)
    implementation(libs.ktlint.plugin)
}
