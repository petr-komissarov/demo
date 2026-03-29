plugins {
    alias(libs.plugins.demo.conventions)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(libs.bundles.koin)
    api(libs.exposed.core)
    api(libs.ktor.serialization.kotlinx.json)
    api(libs.bundles.tinylog)
}
