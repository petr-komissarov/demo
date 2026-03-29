plugins {
    alias(libs.plugins.demo.conventions) apply false
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = libs.versions.gradle.wrapper.get()
}
