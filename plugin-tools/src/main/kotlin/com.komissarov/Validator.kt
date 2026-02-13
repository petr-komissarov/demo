package com.komissarov

import com.komissarov.exceptions.DemoToolException
import net.pwall.json.schema.JSONSchema
import org.tinylog.Logger
import java.io.File

/**
 * Validator.
 */
object Validator {
    /**
     * Get JSON schema errors.
     */
    fun getJsonSchemaErrors(
        file: File,
        schema: String
    ): List<String>? {
        var errors: List<String>? = null

        runCatching {
            file
                .readText()
                .run {
                    errors = JSONSchema
                        .parse(schema)
                        .validateBasic(this)
                        .errors
                        ?.map { exception ->
                            buildString {
                                append(exception.error)
                                append(" - ")
                                append(exception.instanceLocation)
                            }
                        }
                }
        }.onFailure { exception ->
            Logger.error {
                DemoToolException(
                    buildString {
                        append(Validator::class.simpleName)
                        append(": ")
                        append(exception.message)
                    }
                )
            }
        }

        return errors
    }

    /**
     * Check file is readable.
     */
    fun isFileReadable(file: File): Boolean {
        var isReadable = false

        runCatching {
            file.run {
                when {
                    exists().not() or isFile.not() -> {
                        Logger.error {
                            DemoToolException(
                                buildString {
                                    append(Validator::class.simpleName)
                                    append(": file ")
                                    append(file)
                                    append(" not found")
                                }
                            )
                        }
                    }

                    canRead().not() -> {
                        Logger.error {
                            DemoToolException(
                                buildString {
                                    append(Validator::class.simpleName)
                                    append(": unable to read file ")
                                    append(file)
                                }
                            )
                        }
                    }

                    else -> {
                        isReadable = true
                    }
                }
            }
        }.onFailure { exception ->
            Logger.error {
                DemoToolException(
                    buildString {
                        append(Validator::class.simpleName)
                        append(": ")
                        append(exception.message)
                    }
                )
            }
        }

        return isReadable
    }
}
