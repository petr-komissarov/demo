package com.komissarov.tasks

import com.komissarov.Validator
import com.komissarov.actions.ProcessEventsAction
import com.komissarov.containers.ProcessEventsContainer
import com.komissarov.data.dto.settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.tinylog.Logger
import javax.inject.Inject

/**
 * Process events task.
 */
internal abstract class ProcessEventsTask
    @Inject
    constructor() : DefaultTask() {
        /**
         * File chunk size.
         */
        @get:Input
        abstract val chunkSize: Property<Int>

        /**
         * File path.
         */
        @get:Incremental
        @get:InputFile
        @get:PathSensitive(PathSensitivity.ABSOLUTE)
        abstract val eventsJson: RegularFileProperty

        /**
         * DB connection string.
         */
        @get:Input
        abstract val connString: Property<String>

        /**
         * Http base url.
         */
        @get:Input
        abstract val baseUrl: Property<String>

        /**
         * Kafka bootstrap servers.
         */
        @get:Input
        abstract val bootstrapServers: Property<String>

        /**
         * Kafka max block ms config.
         */
        @get:Input
        abstract val maxBlockMSConfig: Property<Int>

        /**
         * Kafka retries config.
         */
        @get:Input
        abstract val retriesConfig: Property<Int>

        /**
         * Kafka topic.
         */
        @get:Input
        abstract val topic: Property<String>

        /**
         * Kafka transactional id config.
         */
        @get:Input
        abstract val transactionalIdConfig: Property<String>

        /**
         * Kafka concurrency limit.
         */
        @get:Input
        abstract val concurrencyLimit: Property<Int>

        /**
         * Output directory.
         */
        @get:OutputDirectory
        abstract val outputDir: DirectoryProperty

        init {
            description = "Process events from a JSON file"
            group = "demo"
        }

        /**
         * Execute process events task.
         */
        @TaskAction
        internal fun execute(inputChanges: InputChanges) {
            when {
                inputChanges.isIncremental and inputChanges
                    .getFileChanges(eventsJson)
                    .any { fileChange -> fileChange.changeType == ChangeType.REMOVED } -> {
                    return
                }

                else -> {
                    runBlocking {
                        eventsJson
                            .get()
                            .asFile
                            .takeIf { file -> Validator.isFileReadable(file) }
                            ?.run {
                                Logger.info {
                                    buildString {
                                        append("✅ Processing the ")
                                        append(this@run)
                                        append(" file")
                                    }
                                }

                                val settings = Settings(
                                    FileSettings(
                                        chunkSize.get(),
                                        this
                                    ),
                                    DBSettings(
                                        connString.get()
                                    ),
                                    HttpSettings(
                                        baseUrl.get()
                                    ),
                                    KafkaSettings(
                                        bootstrapServers.get(),
                                        maxBlockMSConfig.get(),
                                        retriesConfig.get(),
                                        topic.get(),
                                        transactionalIdConfig.get()
                                    ),
                                    Dispatchers.IO.limitedParallelism(concurrencyLimit.get())
                                )

                                ProcessEventsContainer(settings).use { container ->
                                    container
                                        .getInstance<ProcessEventsAction>()
                                        ?.execute()
                                }
                            }
                    }
                }
            }
        }
    }
