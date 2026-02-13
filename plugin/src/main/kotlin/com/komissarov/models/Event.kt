package com.komissarov.models

internal data class Event(
    val id: Int,
    val eventName: String,
    val timestamp: String,
    val actionType: String,
    val payload: Payload
)
