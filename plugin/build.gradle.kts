import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
}

group = "com.komissarov"
version = libs.versions.plugin.get()

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.database)
    implementation(libs.gson)
    implementation(libs.kafka.client)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.logging)
    implementation(libs.okhttp3)
    testImplementation(libs.bundles.cucumber)
    testImplementation(libs.bundles.junit5)
    testImplementation(libs.datafaker)
    testImplementation(libs.mockk)
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

gradlePlugin {
    website = "https://github.com/petr-komissarov/demo/blob/main/README.md"
    vcsUrl = "https://github.com/petr-komissarov/demo.git"

    plugins {
        create("demo") {
            description = "Demo plugin"
            displayName = "Demo"
            id = "${project.group}.demo"
            implementationClass = "${project.group}.DemoPlugin"
            tags = setOf("QA")
        }
    }
}

tasks.test {
    failOnNoDiscoveredTests = false
    ignoreFailures = true
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
    outputs.upToDateWhen { false }

    reports {
        html.required = false
        junitXml.required = false
    }

    systemProperties(
        mapOf(
            "cucumber.execution.parallel.enabled" to true,
            "cucumber.glue" to "com.komissarov.steps",
            "cucumber.junit-platform.naming-strategy" to "long",
            "cucumber.publish.quiet" to "true",
            "cucumber.snippet-type" to "camelcase"
        )
    )

    testLogging {
        displayGranularity = 3
        events("PASSED", "SKIPPED", "FAILED", "STANDARD_OUT")
        exceptionFormat = TestExceptionFormat.FULL
    }

    useJUnitPlatform()
}

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = libs.versions.gradle.get()
}
