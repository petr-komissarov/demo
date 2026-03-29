import org.gradle.plugin.compatibility.compatibility

plugins {
    alias(libs.plugins.demo.conventions)
    alias(libs.plugins.gradle.plugin.compatibility)
    alias(libs.plugins.gradle.plugin.publish)
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
