package com.komissarov.containers

import com.komissarov.HandlerFactory
import com.komissarov.JsonReader
import com.komissarov.actions.ProcessEventsAction
import com.komissarov.base.Container
import com.komissarov.data.dto.events.Payload
import com.komissarov.data.dto.settings.Settings
import com.komissarov.exceptions.DemoContainerException
import com.komissarov.handlers.DBHandler
import com.komissarov.handlers.HttpHandler
import com.komissarov.handlers.KafkaHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.includes
import org.koin.dsl.lazyModule
import org.koin.dsl.onClose
import org.tinylog.Logger
import java.util.Properties

/**
 * Container for process events task.
 */
class ProcessEventsContainer(
    private val settings: Settings
) : Container() {
    @OptIn(ExperimentalSerializationApi::class)
    private val miscModule = lazyModule {
        single { settings }
        single {
            Json {
                allowComments = true
                allowTrailingComma = true
                classDiscriminator = "#class"
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
                serializersModule = SerializersModule {
                    polymorphic(Payload::class) {
                        subclass(Payload.DB::class, Payload.DB.serializer())
                        subclass(Payload.Http::class, Payload.Http.serializer())
                        subclass(Payload.Kafka::class, Payload.Kafka.serializer())
                    }
                }
            }
        }
        singleOf(
            ::JsonReader
        ) onClose { reader ->
            reader?.close()
        }
    }

    private val dbHandlerModule = lazyModule {
        single {
            Database.connect(settings.db.connString, driver = "org.h2.Driver")
        } onClose { database ->
            database
                ?.runCatching {
                    TransactionManager.closeAndUnregister(this)
                }?.onFailure { exception ->
                    Logger.error {
                        DemoContainerException(
                            buildString {
                                append(ProcessEventsContainer::class.simpleName)
                                append(": ")
                                append(exception.message)
                            }
                        )
                    }
                }
        }
        singleOf(::DBHandler)
    }

    private val httpHandlerModule = lazyModule {
        single {
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(json = get())
                }
            }
        } onClose { client ->
            runBlocking {
                client
                    ?.runCatching {
                        close()
                        coroutineContext.job.join()
                    }?.onFailure { exception ->
                        Logger.error {
                            DemoContainerException(
                                buildString {
                                    append(ProcessEventsContainer::class.simpleName)
                                    append(": ")
                                    append(exception.message)
                                }
                            )
                        }
                    }
            }
        }
        singleOf(::HttpHandler)
    }

    private val kafkaHandlerModule = lazyModule {
        single {
            with(Properties()) {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, settings.kafka.bootstrapServers)
                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.getName())
                put(ProducerConfig.MAX_BLOCK_MS_CONFIG, settings.kafka.maxBlockMSConfig)
                put(ProducerConfig.RETRIES_CONFIG, settings.kafka.retriesConfig)
                put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, settings.kafka.transactionalIdConfig)
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.getName())

                KafkaProducer<String, String>(this).apply {
                    runCatching {
                        initTransactions()
                    }.onFailure { exception ->
                        Logger.error {
                            DemoContainerException(
                                buildString {
                                    append(ProcessEventsContainer::class.simpleName)
                                    append(": ")
                                    append(exception.message)
                                }
                            )
                        }
                    }
                }
            }
        } onClose { producer ->
            producer
                ?.runCatching {
                    close()
                }?.onFailure { exception ->
                    Logger.error {
                        DemoContainerException(
                            buildString {
                                append(ProcessEventsContainer::class.simpleName)
                                append(": ")
                                append(exception.message)
                            }
                        )
                    }
                }
        }
        singleOf(::KafkaHandler)
    }

    private val handlersModule = lazyModule {
        includes(
            dbHandlerModule,
            httpHandlerModule,
            kafkaHandlerModule
        )
        singleOf(::HandlerFactory)
    }

    private val actionsModule = lazyModule {
        singleOf(::ProcessEventsAction)
    }

    override val appModule = lazyModule {
        includes(
            miscModule,
            handlersModule,
            actionsModule
        )
    }
}
