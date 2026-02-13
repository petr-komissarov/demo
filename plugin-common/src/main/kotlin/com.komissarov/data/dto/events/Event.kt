package com.komissarov.data.dto.events

import com.komissarov.data.enums.ActionType
import kotlinx.serialization.Serializable

/**
 * Event model.
 */
@Serializable
data class Event(
    val id: Int,
    val eventName: String,
    val timestamp: String,
    val actionType: ActionType,
    val payload: Payload
)
