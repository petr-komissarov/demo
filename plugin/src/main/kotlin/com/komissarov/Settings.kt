package com.komissarov

internal object Settings {
    object ActionTypes {
        const val HTTP = "HTTP"
        const val DB = "DB"
        const val KAFKA = "KAFKA"
    }

    object Http {
        val BASE_URL: String = System.getProperty("HTTP_BASE_URL", "http://localhost:8080")
    }

    object DB {
        val BASE_URL: String = System.getProperty("DB_BASE_URL", "jdbc:h2:mem:test")
    }

    object Kafka {
        val BASE_URL: String = System.getProperty("KAFKA_BASE_URL", "localhost:9092")
        val MAX_BLOCK_MS_CONFIG: String = System.getProperty("KAFKA_MAX_BLOCK_MS_CONFIG", "1000")
        val TOPIC: String = System.getProperty("KAFKA_TOPIC", "events-topic")
    }
}
