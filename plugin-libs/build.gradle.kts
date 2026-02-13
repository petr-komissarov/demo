import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

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
    api(project(":plugin-common"))
    implementation(libs.json.kotlin.schema)
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
                url = "https://github.com/petr-komissarov/demo/tree/main/plugin-libs"

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
