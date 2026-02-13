package com.komissarov

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.komissarov.models.Event
import com.komissarov.services.DB
import com.komissarov.services.Http
import com.komissarov.services.Kafka
import com.komissarov.tools.Handler
import com.komissarov.tools.Json
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.use

private val logger by lazy { KotlinLogging.logger {} }

internal fun run(projectPath: String, relativePath: String) {
    try {
        Json(projectPath, relativePath).reader?.use { jsonReader ->
            runBlocking {
                val gson = Gson()
                val events = gson.fromJson<Array<Event>>(jsonReader, object : TypeToken<Array<Event>>() {}.type)
                val http = Http(gson)
                val db = DB()

                Kafka(gson).use { kafka ->
                    val jobs = events.map { event -> async { Handler(http, db, kafka).processEvent(event) } }
                    jobs.awaitAll()
                }
            }
        }
    } catch (e: Exception) {
        when (e) {
            is JsonIOException, is JsonSyntaxException -> {
                logger.error(e) { "Deserialization exception!" }
            }

            else -> logger.error(e) { "Unexpected deserialization exception!" }
        }
    }
}
