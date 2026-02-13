package com.komissarov.extensions

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * Kafka extension.
 */
abstract class KafkaExtension
    @Inject
    constructor(
        objects: ObjectFactory
    ) {
        /**
         * Kafka bootstrap servers.
         */
        val bootstrapServers = objects
            .property(String::class.java)
            .convention("localhost:9092")

        /**
         * Kafka max block ms config.
         */
        val maxBlockMSConfig = objects
            .property(Int::class.java)
            .convention(10)

        /**
         * Kafka retry config.
         */
        val retriesConfig = objects
            .property(Int::class.java)
            .convention(1)

        /**
         * Kafka topic.
         */
        val topic = objects
            .property(String::class.java)
            .convention("events-topic")

        /**
         * Kafka transactional id config.
         */
        val transactionalIdConfig = objects
            .property(String::class.java)
            .convention("demo")
    }
