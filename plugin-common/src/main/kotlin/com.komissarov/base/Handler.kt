package com.komissarov.base

/**
 * Handler interface.
 */
interface Handler<out T> {
    /**
     * Process event.
     */
    suspend fun processEvent(payload: @UnsafeVariance T)
}
