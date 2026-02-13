package com.komissarov.data.dto.settings

import java.io.File

/**
 * File settings.
 */
data class FileSettings(
    val chunkSize: Int,
    val eventsJson: File
)
