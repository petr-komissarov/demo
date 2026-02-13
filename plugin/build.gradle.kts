import org.gradle.plugin.compatibility.compatibility
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.gradle.plugin.compatibility)
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint.plugin)
    `maven-publish`
}

group = libs.versions.group.get()
version = libs.versions.plugin.get()

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

configure<KtlintExtension> {
    ignoreFailures = true
    outputColorName = "RED"
    version = libs.versions.ktlint.tool
        .get()

    reporters {
        reporter(ReporterType.HTML)
    }
}

dependencies {
    implementation(project(":plugin-core"))
}

gradlePlugin {
    plugins {
        register("demo") {
            compatibility {
                features {
                    configurationCache = true
                }
            }

            description = "Process events from a JSON file"
            displayName = "Demo"

            id = buildString {
                append(libs.versions.group.get())
                append(".demo")
            }

            implementationClass = buildString {
                append(libs.versions.group.get())
                append(".DemoPlugin")
            }

            tags = listOf("demo", "event processor")
            vcsUrl = "https://github.com/petr-komissarov/demo.git"
            website = "https://github.com/petr-komissarov/demo/blob/main/README.md"
        }
    }
}
