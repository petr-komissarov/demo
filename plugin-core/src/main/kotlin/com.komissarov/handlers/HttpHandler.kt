package com.komissarov.handlers

import com.komissarov.base.Handler
import com.komissarov.data.dto.events.Payload
import com.komissarov.data.dto.settings.Settings
import com.komissarov.exceptions.DemoHandlerException
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.withContext
import org.tinylog.Logger

/**
 * Http handler.
 */
class HttpHandler(
    private val client: HttpClient,
    private val settings: Settings
) : Handler<Payload.Http> {
    /**
     * Post event.
     */
    override suspend fun processEvent(payload: Payload.Http) {
        withContext(settings.dispatcher) {
            runCatching {
                val response = client.post(
                    buildString {
                        append(settings.http.baseUrl)
                        append("/api/events")
                    }
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(payload)
                }

                if (response.status.isSuccess().not()) {
                    Logger.error {
                        DemoHandlerException(
                            buildString {
                                append(HttpHandler::class.simpleName)
                                append(": status = ")
                                append(response.status.value)
                                append(", description = ")
                                append(response.status.description)
                            }
                        )
                    }
                }
            }.onFailure { exception ->
                Logger.error {
                    DemoHandlerException(
                        buildString {
                            append(HttpHandler::class.simpleName)
                            append(": ")
                            append(exception.message)
                        }
                    )
                }
            }
        }
    }
}
