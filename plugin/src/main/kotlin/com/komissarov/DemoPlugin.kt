package com.komissarov

import com.komissarov.exceptions.DemoPluginException
import com.komissarov.extensions.DemoExtension
import com.komissarov.tasks.ProcessEventsTask
import com.komissarov.tasks.ValidateSchemaTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.tinylog.Logger
import java.io.File

/**
 * Gradle plugin.
 */
@Suppress("UNUSED_PARAMETER")
class DemoPlugin : Plugin<Project> {
    /**
     * Apply plugin.
     */
    override fun apply(project: Project) {
        project
            .runCatching {
                val extension = extensions.create(
                    "demo",
                    DemoExtension::class.java,
                    objects
                )

                tasks.register("processEvents", ProcessEventsTask::class.java) { task ->
                    task.chunkSize.set(extension.file.chunkSize)
                    task.eventsJson.set(
                        project.layout.file(
                            extension.file.eventsJson.map { eventsJson ->
                                File(
                                    eventsJson
                                )
                            }
                        )
                    )
                    task.connString.set(extension.db.connString)
                    task.baseUrl.set(extension.http.baseUrl)
                    task.bootstrapServers.set(extension.kafka.bootstrapServers)
                    task.maxBlockMSConfig.set(extension.kafka.maxBlockMSConfig)
                    task.retriesConfig.set(extension.kafka.retriesConfig)
                    task.topic.set(extension.kafka.topic)
                    task.transactionalIdConfig.set(extension.kafka.transactionalIdConfig)
                    task.concurrencyLimit.set(extension.concurrencyLimit)
                    task.outputDir.set(project.layout.buildDirectory.dir("demo"))
                }

                tasks.register("validateSchema", ValidateSchemaTask::class.java) { task ->
                    task.eventsJson.set(
                        project.layout.file(
                            extension.file.eventsJson.map { eventsJson ->
                                File(
                                    eventsJson
                                )
                            }
                        )
                    )
                    task.outputDir.set(project.layout.buildDirectory.dir("demo"))
                }
            }.onFailure { exception ->
                Logger.error {
                    DemoPluginException(
                        buildString {
                            append(DemoPlugin::class.simpleName)
                            append(": ")
                            append(exception.message)
                        }
                    )
                }
            }
    }
}
