import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.time.Duration
import java.util.Locale

plugins {
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
    api(project(":plugin-tools"))
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.ktor)
    implementation(libs.kafka.clients)
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = project.name
            groupId = libs.versions.group.get()
            version = libs.versions.plugin.get()

            from(components["java"])

            pom {
                name = project.name
                url = "https://github.com/petr-komissarov/demo/tree/main/plugin-core"

                developers {
                    developer {
                        id = "petr-komissarov"
                        name = "Peter Komissarov"
                        email = "job-komissarov@yandex.ru"
                    }
                }

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/petr-komissarov/demo/blob/main/LICENSE"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/petr-komissarov/demo.git"
                    developerConnection = "scm:git:ssh://github.com/petr-komissarov/demo.git"
                    url = "https://github.com/petr-komissarov/demo"
                }
            }
        }
    }
}

@Suppress("UnstableApiUsage")
testing.suites.withType<JvmTestSuite> {
    dependencies {
        implementation(libs.assertj)
        implementation(libs.cucumber.java)
        implementation(libs.cucumber.junit.platform.engine)
        implementation(libs.datafaker)
        implementation(libs.mockk)
        implementation(libs.slf4j.nop)
    }

    targets.all {
        testTask.configure {
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
                    "cucumber.execution.execution-mode.feature" to "concurrent",
                    "cucumber.execution.parallel.enabled" to "true",
                    "cucumber.glue" to buildString {
                        append(libs.versions.group.get())
                        append(".steps,")
                        append(libs.versions.group.get())
                        append(".hooks")
                    },
                    "cucumber.junit-platform.naming-strategy" to "long",
                    "cucumber.plugin" to "html:build/reports/cucumber.html,pretty",
                    "cucumber.snippet-type" to "camelcase"
                )
            )

            // Filter tags
            providers
                .gradleProperty("tags")
                .orNull
                ?.takeUnless { tags -> tags.isBlank() }
                ?.run { systemProperty("cucumber.filter.tags", this) }

            testDefinitionDirs.from("src/test/resources/features")

            testLogging {
                events("STANDARD_OUT")
                displayGranularity = 3
                showCauses = false
                showExceptions = false
                showStackTraces = false
            }

            useJUnitJupiter()

            // Print summary at the end
            addTestListener(object : TestListener {
                override fun beforeSuite(suite: TestDescriptor) {}

                override fun beforeTest(testDescriptor: TestDescriptor) {}

                override fun afterTest(
                    testDescriptor: TestDescriptor,
                    result: TestResult
                ) {
                }

                override fun afterSuite(
                    suite: TestDescriptor,
                    result: TestResult
                ) {
                    suite
                        .takeIf { descriptor -> descriptor.parent == null }
                        ?.runCatching {
                            val header = buildString {
                                append("|  ")
                                append(this@runCatching.name)
                            }

                            val body = buildString {
                                append("|  Result: ")
                                append(result.resultType)
                                append(" (")
                                append(result.testCount)
                                append(" tests, ")
                                append(result.successfulTestCount)
                                append(" successes, ")
                                append(result.failedTestCount)
                                append(" failures, ")
                                append(result.skippedTestCount)
                                append(" skipped)")
                            }

                            val footer = buildString {
                                append("|  Duration: ")
                                append(
                                    Duration
                                        .ofMillis(result.endTime - result.startTime)
                                        .toString()
                                        .substring(2)
                                        .lowercase(Locale.ENGLISH)
                                )
                            }

                            val lines = arrayOf(header, body, footer)
                            val maxLength = lines.maxOf { line -> line.length }

                            lines.forEachIndexed { index, value ->
                                lines[index] = buildString {
                                    append(value)
                                    append(" ".repeat(maxLength - value.length))
                                    append("  |")
                                }
                            }

                            val horizontalBoarder = buildString {
                                append(" ")
                                append("—".repeat(lines[0].length - 2))
                                append(" ")
                            }

                            println(
                                buildString {
                                    appendLine(horizontalBoarder)
                                    appendLine(
                                        lines.joinToString(
                                            separator = System.lineSeparator()
                                        )
                                    )
                                    appendLine(horizontalBoarder)
                                }
                            )
                        }?.onFailure { exception ->
                            error(exception)
                        }
                }
            })
        }
    }
}
