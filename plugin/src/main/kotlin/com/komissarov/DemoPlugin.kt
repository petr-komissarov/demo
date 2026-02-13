package com.komissarov

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UNUSED_PARAMETER")
class DemoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("demo", DemoExtension::class.java)

        project.tasks.register("runDemo") { task ->
            task.description = "Run demo task"
            task.group = "demo"

            task.doLast {
                run(
                    project.path,
                    extension.relativePath.getOrElse("events.json")
                )
            }
        }
    }
}
