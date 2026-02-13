package com.komissarov.exceptions

/**
 * Base exception.
 */
abstract class DemoException(
    message: String?
) : Exception(
        buildString {
            append("❌ ")
            append(message)
        }
    )
