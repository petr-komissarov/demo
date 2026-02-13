package com.komissarov.services

import com.komissarov.Settings
import com.komissarov.models.Payload
import com.komissarov.tables.Transactions
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.sql.SQLException

internal class DB {
    companion object {
        private val logger by lazy { KotlinLogging.logger {} }

        init {
            Database.connect(Settings.DB.BASE_URL, driver = "org.h2.Driver")
        }
    }

    suspend fun update(payload: Payload) = withContext(Dispatchers.IO) {
        var result: Int? = null

        try {
            transaction {
                Transactions.update({ Transactions.transactionId eq payload.transactionId!! }) {
                    it[amount] = payload.amount!!
                    it[currency] = payload.currency!!
                    it[status] = payload.status!!
                }
            }

            result = 2
        } catch (e: Exception) {
            when (e) {
                is IllegalStateException, is SQLException -> {
                    logger.error(e) { "DB exception!" }
                }

                else -> logger.error(e) { "Unexpected db exception!" }
            }
        }

        return@withContext result
    }
}
