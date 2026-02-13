package com.komissarov.extensions

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * Plugin extension.
 */
abstract class DemoExtension
    @Inject
    constructor(
        objects: ObjectFactory
    ) {
        /**
         * File extension.
         */
        val file = objects.newInstance(FileExtension::class.java)

        /**
         * DB extension.
         */
        val db = objects.newInstance(DBExtension::class.java)

        /**
         * Http extension.
         */
        val http = objects.newInstance(HttpExtension::class.java)

        /**
         * Kafka extension.
         */
        val kafka = objects.newInstance(KafkaExtension::class.java)

        /**
         * Concurrency limit for coroutine dispatcher.
         */
        val concurrencyLimit = objects
            .property(Int::class.java)
            .convention(Runtime.getRuntime().availableProcessors().coerceAtLeast(64))

        /**
         * Change file extension.
         */
        @Suppress("UNUSED")
        fun file(action: Action<in FileExtension>) {
            action.execute(file)
        }

        /**
         * Change db extension.
         */
        @Suppress("UNUSED")
        fun db(action: Action<in DBExtension>) {
            action.execute(db)
        }

        /**
         * Change http extension.
         */
        @Suppress("UNUSED")
        fun http(action: Action<in HttpExtension>) {
            action.execute(http)
        }

        /**
         * Change kafka extension.
         */
        @Suppress("UNUSED")
        fun kafka(action: Action<in KafkaExtension>) {
            action.execute(kafka)
        }
    }
