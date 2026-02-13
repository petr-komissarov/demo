package com.komissarov

import com.komissarov.base.Handler
import com.komissarov.data.dto.events.Payload
import com.komissarov.data.enums.ActionType
import com.komissarov.handlers.DBHandler
import com.komissarov.handlers.HttpHandler
import com.komissarov.handlers.KafkaHandler

/**
 * Handler factory.
 */
class HandlerFactory(
    private val dbHandler: DBHandler,
    private val httpHandler: HttpHandler,
    private val kafkaHandler: KafkaHandler
) {
    /**
     * Get handler by action type.
     */
    fun getHandler(actionType: ActionType): Handler<Payload>? =
        when (actionType) {
            ActionType.DB -> dbHandler
            ActionType.HTTP -> httpHandler
            ActionType.KAFKA -> kafkaHandler
        }
}
