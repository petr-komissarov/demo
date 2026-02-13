package com.komissarov.services

import com.google.gson.Gson
import com.komissarov.Settings
import com.komissarov.models.Payload
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.InterruptException
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

internal class Kafka(private val gson: Gson) : AutoCloseable {
    companion object {
        private val logger by lazy { KotlinLogging.logger {} }
        private val producer = lazy {
            val props = Properties().apply {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Settings.Kafka.BASE_URL)
                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.getName())
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.getName())
                put(ProducerConfig.MAX_BLOCK_MS_CONFIG, Settings.Kafka.MAX_BLOCK_MS_CONFIG)
            }

            KafkaProducer<String, String>(props)
        }
    }

    suspend fun send(payload: Payload) = withContext(Dispatchers.IO) {
        var result: Int? = null

        try {
            val value = gson.toJson(payload.toKafkaMap())
            val record = ProducerRecord<String, String>(Settings.Kafka.TOPIC, value)

            producer.value.send(record) { _: RecordMetadata?, exception: Exception? ->
                if (exception == null) {
                    result = 3
                } else {
                    logger.error(exception) { "Kafka exception!" }
                }
            }.get()
        } catch (e: Exception) {
            when (e) {
                is IllegalStateException, is InterruptException, is SerializationException, is KafkaException -> {
                    logger.error(e) { "Kafka exception!" }
                }

                else -> logger.error(e) { "Unexpected kafka exception!" }
            }
        }

        return@withContext result
    }

    override fun close() {
        if (producer.isInitialized()) {
            producer.value.close()
        }
    }
}
