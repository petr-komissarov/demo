package com.komissarov.handlers

import com.komissarov.base.Handler
import com.komissarov.data.dto.events.Payload
import com.komissarov.data.dto.settings.Settings
import com.komissarov.exceptions.DemoHandlerException
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.tinylog.Logger

/**
 * Kafka handler.
 */
class KafkaHandler(
    private val json: Json,
    private val producer: KafkaProducer<String, String>,
    private val settings: Settings
) : Handler<Payload.Kafka> {
    /**
     * Send message.
     */
    override suspend fun processEvent(payload: Payload.Kafka) {
        withContext(settings.dispatcher) {
            runCatching {
                val record = with(json.encodeToString(payload)) {
                    ProducerRecord<String, String>(settings.kafka.topic, this)
                }

                with(producer) {
                    beginTransaction()
                    send(record).get()
                    commitTransaction()
                }
            }.onFailure { exception ->
                Logger.error {
                    DemoHandlerException(
                        buildString {
                            append(KafkaHandler::class.simpleName)
                            append(": ")
                            append(exception.message)
                        }
                    )
                }

                producer.runCatching { abortTransaction() }.onFailure { _ -> }
            }
        }
    }
}
