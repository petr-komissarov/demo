package com.komissarov.extensions

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * Http extension.
 */
abstract class HttpExtension
    @Inject
    constructor(
        objects: ObjectFactory
    ) {
        /**
         * Http base url.
         */
        val baseUrl = objects
            .property(String::class.java)
            .convention("http://localhost:8080")
    }
