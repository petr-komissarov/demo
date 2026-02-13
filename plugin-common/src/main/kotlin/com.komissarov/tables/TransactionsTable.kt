package com.komissarov.tables

import org.jetbrains.exposed.v1.core.Table

/**
 * Transactions table.
 */
object TransactionsTable : Table("transactions") {
    @Suppress("UNUSED")
    val id = integer("id").autoIncrement()
    val transactionId = varchar("transactionId", 20)
    val amount = float("amount")
    val currency = varchar("currency", 10)
    val status = varchar("status", 10)
}
