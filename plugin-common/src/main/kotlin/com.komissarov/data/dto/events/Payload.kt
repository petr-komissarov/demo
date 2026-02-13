package com.komissarov.data.dto.events

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Base payload.
 */
@Serializable(with = PayloadSerializer::class)
sealed class Payload {
    /**
     * DB payload.
     */
    @Serializable
    data class DB(
        val transactionId: String,
        val amount: Float,
        val currency: String,
        val status: String
    ) : Payload()

    /**
     * Http payload.
     */
    @Serializable
    data class Http(
        val userId: String,
        val email: String,
        val status: String
    ) : Payload()

    /**
     * Kafka payload.
     */
    @Serializable
    data class Kafka(
        val orderId: String,
        val shippingMethod: String,
        val trackingNumber: String,
        val estimatedDelivery: String
    ) : Payload()
}

/**
 * Payload polymorphic serializer.
 */
object PayloadSerializer : JsonContentPolymorphicSerializer<Payload>(Payload::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Payload> =
        when {
            element.jsonObject["currency"]
                ?.jsonPrimitive
                ?.contentOrNull
                .isNullOrBlank()
                .not() -> {
                Payload.DB.serializer()
            }

            element.jsonObject["status"]
                ?.jsonPrimitive
                ?.contentOrNull
                .isNullOrBlank()
                .not() -> {
                Payload.Http.serializer()
            }

            else -> {
                Payload.Kafka.serializer()
            }
        }
}
