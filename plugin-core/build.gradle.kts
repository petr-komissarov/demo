plugins {
    alias(libs.plugins.demo.conventions)
}

dependencies {
    api(project(":plugin-libs"))
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.ktor)
    implementation(libs.kafka.clients)
}
