package com.komissarov.extensions

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * File extension.
 */
abstract class FileExtension
    @Inject
    constructor(
        objects: ObjectFactory
    ) {
        /**
         * File chunk size.
         */
        val chunkSize = objects
            .property(Int::class.java)
            .convention(Runtime.getRuntime().availableProcessors().coerceAtLeast(64))

        /**
         * File path.
         */
        val eventsJson = objects
            .property(String::class.java)
            .convention("events.json")
    }
