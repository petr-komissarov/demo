package com.komissarov.tasks

import com.komissarov.Extensions.getResourceText
import com.komissarov.Validator
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.tinylog.Logger
import javax.inject.Inject

/**
 * Validate schema task.
 */
internal abstract class ValidateSchemaTask
    @Inject
    constructor() : DefaultTask() {
        /**
         * File path.
         */
        @get:Incremental
        @get:InputFile
        @get:PathSensitive(PathSensitivity.ABSOLUTE)
        abstract val eventsJson: RegularFileProperty

        /**
         * Output directory.
         */
        @get:OutputDirectory
        abstract val outputDir: DirectoryProperty

        init {
            description = "Validate the JSON file schema"
            group = "demo"
        }

        /**
         * Execute validate schema task.
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
                    val schema = "events.schema.json".getResourceText() ?: return

                    eventsJson
                        .get()
                        .asFile
                        .takeIf { file -> Validator.isFileReadable(file) }
                        ?.run {
                            Validator
                                .getJsonSchemaErrors(this, schema)
                                ?.forEach { exception -> Logger.error { exception } }
                        }
                }
            }
        }
    }
