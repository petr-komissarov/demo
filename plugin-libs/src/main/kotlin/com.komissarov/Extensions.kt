package com.komissarov

import com.komissarov.exceptions.DemoToolException
import org.tinylog.Logger

/**
 * Type extensions.
 */
@Suppress("UNUSED")
object Extensions {
    /**
     * Get resource text.
     */
    fun String.getResourceText(): String? {
        var resource: String? = null

        runCatching {
            resource = object {}
                .javaClass
                .classLoader
                ?.getResourceAsStream(this)
                ?.use { stream ->
                    stream.reader().use { reader -> reader.readText() }
                }

            if (resource == null) {
                Logger.error {
                    DemoToolException(
                        buildString {
                            append(Extensions::class.simpleName)
                            append(": invalid resource ")
                            append(this)
                        }
                    )
                }
            }
        }.onFailure { exception ->
            Logger.error {
                DemoToolException(
                    buildString {
                        append(Extensions::class.simpleName)
                        append(": ")
                        append(exception.message)
                    }
                )
            }
        }

        return resource
    }
}
