import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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
    jvmToolchain(
        libs.versions.java
            .get()
            .toInt()
    )
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
    api(project(":plugin-libs"))
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
                private lateinit var start: LocalDateTime

                override fun beforeSuite(suite: TestDescriptor) {
                    start = LocalDateTime.now()
                }

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
                    runCatching {
                        suite
                            .takeIf { descriptor -> descriptor.parent == null }
                            ?.run {
                                val header = buildString {
                                    append("|  ")
                                    append(this@run.name)
                                }

                                val resultType = when {
                                    result.failedTestCount > 0 -> {
                                        "FAILURE"
                                    }

                                    (result.successfulTestCount > 0).or(result.testCount == 0L) -> {
                                        "SUCCESS"
                                    }

                                    else -> {
                                        "SKIP"
                                    }
                                }

                                val body = buildString {
                                    append("|  Result: ")
                                    append(resultType)
                                    append(" (")
                                    append(result.testCount)
                                    append(" - tests, ")
                                    append(result.successfulTestCount)
                                    append(" - successed, ")
                                    append(result.failedTestCount)
                                    append(" - failed, ")
                                    append(result.skippedTestCount)
                                    append(" - skipped)")
                                }

                                val footer = buildString {
                                    append("|  Duration: ")
                                    append(
                                        ChronoUnit
                                            .MILLIS
                                            .between(start, LocalDateTime.now())
                                    )
                                    append(" milliseconds")
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
                            }
                    }.onFailure { exception ->
                        error(exception)
                    }
                }
            })
        }
    }
}
