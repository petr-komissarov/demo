package com.komissarov.data.dto.settings

/**
 * Kafka settings.
 */
data class KafkaSettings(
    val bootstrapServers: String,
    val maxBlockMSConfig: Int,
    val retriesConfig: Int,
    val topic: String,
    val transactionalIdConfig: String
)
