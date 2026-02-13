package com.komissarov

import com.komissarov.data.dto.settings.Settings
import com.komissarov.exceptions.DemoToolException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence
import org.tinylog.Logger
import java.io.FileInputStream
import java.io.InputStream

/**
 * JSON reader.
 */
class JsonReader(
    val json: Json,
    val settings: Settings
) : AutoCloseable {
    val fileStream = lazy {
        var inputStream: InputStream? = null

        runCatching {
            inputStream = FileInputStream(settings.file.eventsJson)
        }.onFailure { exception ->
            Logger.error {
                DemoToolException(
                    buildString {
                        append(JsonReader::class.simpleName)
                        append(": ")
                        append(exception.message)
                    }
                )
            }
        }

        inputStream
    }

    /**
     * Deserialize a file into a model.
     */
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> decodeJsonToSequence(): Sequence<List<T>>? {
        var sequence: Sequence<List<T>>? = null

        runCatching {
            fileStream
                .value
                ?.run {
                    sequence = json
                        .decodeToSequence<T>(this)
                        .chunked(settings.file.chunkSize)
                }
        }.onFailure { exception ->
            Logger.error {
                DemoToolException(
                    buildString {
                        append(JsonReader::class.simpleName)
                        append(": ")
                        append(exception.message)
                    }
                )
            }
        }

        return sequence
    }

    /**
     * Close stream.
     */
    override fun close() {
        runCatching {
            if (fileStream.isInitialized()) {
                fileStream.value?.close()
            }
        }.onFailure { exception ->
            Logger.error {
                DemoToolException(
                    buildString {
                        append(JsonReader::class.simpleName)
                        append(": ")
                        append(exception.message)
                    }
                )
            }
        }
    }
}
