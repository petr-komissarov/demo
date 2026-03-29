import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("jvm")
    `maven-publish`
}

repositories {
    mavenCentral()
}

private val libs by lazy { extensions.getByType<VersionCatalogsExtension>().named("libs") }

// Versions
private val javaVersion by lazy { libs.findVersion("java").get().toString().toInt() }
private val pluginGroup by lazy { libs.findVersion("group").get().toString() }
private val pluginVersion by lazy { libs.findVersion("plugin").get().toString() }
private val ktlintVersion by lazy { libs.findVersion("ktlint-tool").get().toString() }

// Libraries
private val assertj by lazy { libs.findLibrary("assertj").get() }
private val cucumberJava by lazy { libs.findLibrary("cucumber-java").get() }
private val cucumberJunitPlatformEngine by lazy { libs.findLibrary("cucumber-junit-platform-engine").get() }
private val datafaker by lazy { libs.findLibrary("datafaker").get() }
private val mockk by lazy { libs.findLibrary("mockk").get() }
private val slf4jNop by lazy { libs.findLibrary("slf4j-nop").get() }

group = pluginGroup
version = pluginVersion

kotlin {
    jvmToolchain(javaVersion)
}

configure<KtlintExtension> {
    ignoreFailures = true
    outputColorName = "RED"
    version = ktlintVersion

    reporters {
        reporter(ReporterType.HTML)
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = project.name
            groupId = pluginGroup
            version = pluginVersion

            from(components["java"])

            pom {
                name = project.name
                url = buildString {
                    append("https://github.com/petr-komissarov/demo/tree/main/")
                    append(project.name)
                }

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
        implementation(assertj)
        implementation(cucumberJava)
        implementation(cucumberJunitPlatformEngine)
        implementation(datafaker)
        implementation(mockk)
        implementation(slf4jNop)
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
                        append(pluginGroup)
                        append(".steps,")
                        append(pluginGroup)
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
                                    append(result.endTime - result.startTime)
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
