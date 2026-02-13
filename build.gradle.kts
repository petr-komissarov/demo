plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = libs.versions.gradle.wrapper.get()
}
