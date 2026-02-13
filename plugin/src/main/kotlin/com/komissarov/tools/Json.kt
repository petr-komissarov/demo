package com.komissarov.tools

import com.google.gson.stream.JsonReader
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.FileReader

internal class Json(projectPath: String, relativePath: String) : AutoCloseable {
    companion object {
        private val logger by lazy { KotlinLogging.logger {} }
    }

    var reader: JsonReader? = null

    init {
        if (relativePath.isBlank()) {
            logger.error { "relativePath must not be empty!" }
        } else {
            val files = setOf(File(projectPath, relativePath), File(relativePath))
            val file = files.firstOrNull { file -> file.exists() && file.isFile && file.canRead() }

            if (file == null) {
                logger.error { "Unable to read file $relativePath!" }
            } else {
                logger.warn { "Processing the $relativePath file" }
                reader = JsonReader(FileReader(file))
            }
        }
    }

    override fun close() {
        reader?.close()
    }
}
