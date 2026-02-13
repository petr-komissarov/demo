package com.komissarov.models

internal data class Payload(
    val userId: String? = null,
    val email: String? = null,
    val status: String? = null,
    val transactionId: String? = null,
    val amount: Float? = null,
    val currency: String? = null,
    val orderId: String? = null,
    val shippingMethod: String? = null,
    val trackingNumber: String? = null,
    val estimatedDelivery: String? = null
) {
    fun toHttpMap(): Map<String, String?> = mapOf(
        "userId" to userId,
        "email" to email,
        "status" to status
    )

    fun toKafkaMap(): Map<String, String?> = mapOf(
        "orderId" to orderId,
        "shippingMethod" to shippingMethod,
        "trackingNumber" to trackingNumber,
        "estimatedDelivery" to estimatedDelivery
    )
}
