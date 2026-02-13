package com.komissarov.tools

import com.komissarov.Settings
import com.komissarov.models.Event
import com.komissarov.services.DB
import com.komissarov.services.Http
import com.komissarov.services.Kafka

internal class Handler(private val http: Http, private val db: DB, private val kafka: Kafka) {
    suspend fun processEvent(event: Event): Int? {
        var result: Int? = null

        when (event.actionType) {
            Settings.ActionTypes.HTTP -> {
                result = http.post(event.payload)
            }

            Settings.ActionTypes.DB -> {
                result = db.update(event.payload)
            }

            Settings.ActionTypes.KAFKA -> {
                result = kafka.send(event.payload)
            }
        }

        return result
    }
}
