package com.komissarov.tables

import org.jetbrains.exposed.v1.core.Table

internal object Transactions : Table("transactions") {
    @Suppress("unused")
    val id = integer("id").autoIncrement()
    val amount = float("amount")
    val currency = varchar("currency", 10)
    val transactionId = varchar("transactionId", 20)
    val status = varchar("status", 10)
}
