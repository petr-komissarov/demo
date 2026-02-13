package com.komissarov.data.dto.settings

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Settings.
 */
data class Settings(
    val file: FileSettings,
    val db: DBSettings,
    val http: HttpSettings,
    val kafka: KafkaSettings,
    val dispatcher: CoroutineDispatcher
)
