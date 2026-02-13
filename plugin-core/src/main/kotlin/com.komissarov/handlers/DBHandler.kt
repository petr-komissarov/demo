package com.komissarov.handlers

import com.komissarov.base.Handler
import com.komissarov.data.dto.events.Payload
import com.komissarov.data.dto.settings.Settings
import com.komissarov.exceptions.DemoHandlerException
import com.komissarov.tables.TransactionsTable
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update
import org.tinylog.Logger

/**
 * Database handler.
 */
@Suppress("UNUSED")
class DBHandler(
    private val database: Database,
    private val settings: Settings
) : Handler<Payload.DB> {
    /**
     * Update transactions.
     */
    override suspend fun processEvent(payload: Payload.DB) {
        withContext(settings.dispatcher) {
            runCatching {
                suspendTransaction {
                    TransactionsTable.update({ TransactionsTable.transactionId eq payload.transactionId }) { table ->
                        table[amount] = payload.amount
                        table[currency] = payload.currency
                        table[status] = payload.status
                    }
                }
            }.onFailure { exception ->
                Logger.error {
                    DemoHandlerException(
                        buildString {
                            append(DBHandler::class.simpleName)
                            append(": ")
                            append(exception.message)
                        }
                    )
                }
            }
        }
    }
}
