package com.komissarov.extensions

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * DB extension.
 */
abstract class DBExtension
    @Inject
    constructor(
        objects: ObjectFactory
    ) {
        /**
         * DB connection string.
         */
        val connString = objects
            .property(String::class.java)
            .convention("jdbc:h2:mem:test")
    }
