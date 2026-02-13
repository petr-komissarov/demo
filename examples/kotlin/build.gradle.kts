plugins {
    id("java")
    id("com.komissarov.demo") version "1.0.0"
}

demo {
    file {
        chunkSize = Runtime.getRuntime().availableProcessors().coerceAtLeast(64)
        eventsJson = "events.json"
    }

    db {
        connString = "jdbc:h2:mem:test"
    }

    http {
        baseUrl = "http://localhost:8080"
    }

    kafka {
        bootstrapServers = "localhost:9092"
        maxBlockMSConfig = 1000
        retriesConfig = 1
        topic = "events-topic"
        transactionalIdConfig = "demo"
    }

    concurrencyLimit = Runtime.getRuntime().availableProcessors().coerceAtLeast(64)
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
