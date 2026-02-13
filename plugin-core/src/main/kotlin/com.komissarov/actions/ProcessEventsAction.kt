package com.komissarov.actions

import com.komissarov.HandlerFactory
import com.komissarov.JsonReader
import com.komissarov.data.dto.events.Event
import com.komissarov.exceptions.DemoActionException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.tinylog.Logger

/**
 * Process events action.
 */
class ProcessEventsAction(
    private val handlerFactory: HandlerFactory,
    private val jsonReader: JsonReader
) {
    /**
     * Execute process events action.
     */
    suspend fun execute() {
        coroutineScope {
            var totalEventsCount = 0

            runCatching {
                jsonReader.decodeJsonToSequence<Event>()?.forEach { chunk ->
                    chunk
                        .map { event ->
                            launch {
                                handlerFactory.getHandler(event.actionType)?.processEvent(event.payload)
                            }
                        }.joinAll()

                    totalEventsCount += chunk.size

                    Logger.info {
                        buildString {
                            append("✅ ")
                            append(totalEventsCount)
                            append(" events processed")
                        }
                    }
                }
            }.onFailure { exception ->
                Logger.error {
                    DemoActionException(
                        buildString {
                            append(ProcessEventsAction::class.simpleName)
                            append(": ")
                            append(exception.message)
                        }
                    )
                }
            }
        }
    }
}
