plugins {
    alias(libs.plugins.demo.conventions)
}

dependencies {
    api(project(":plugin-common"))
    implementation(libs.json.kotlin.schema)
}
