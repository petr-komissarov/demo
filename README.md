[![publish.ci](https://github.com/petr-komissarov/demo/actions/workflows/publish.yml/badge.svg)](https://github.com/petr-komissarov/demo/actions/workflows/publish.yml)
[![test.ci](https://github.com/petr-komissarov/demo/actions/workflows/test.yml/badge.svg)](https://github.com/petr-komissarov/demo/actions/workflows/test.yml)
<br><br>
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka)
![Ktor](https://img.shields.io/badge/Ktor-%23087CFA.svg?style=for-the-badge&logo=Ktor&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-f5f5f5?style=for-the-badge&logo=junit5&logoColor=dc524a)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)

# 🚀 Demo Gradle Plugin

> **⚠️ Important:** This Gradle plugin is intended **for testing and demonstration purposes only**.

A flexible Gradle plugin designed to process a stream of events defined in a JSON configuration file. Based on the event
type, it can interact with a database, send HTTP requests, or produce messages to Apache Kafka.

## ✨ Features

The plugin introduces the `processEvents` task, which reads a JSON file and executes a specific action for each event
based on its `actionType`:

* **`DB`** : Executes an SQL query against a database.
* **`HTTP`** : Sends an HTTP request to a specified endpoint.
* **`KAFKA`** : Publishes a message to an Apache Kafka topic.

## ⚙️ Configuration

### 1. Events File (`events.json`)

Create a JSON file (e.g., `events.json`) in the root of your project. This file should contain an array of event
objects. Here's an example:

```json
[
    {
        "id": 1,
        "eventName": "Payment Processed",
        "timestamp": "2024-01-15T11:45:00Z",
        "actionType": "DB",
        "payload": {
            "transactionId": "txn_789",
            "amount": 99.99,
            "currency": "USD",
            "status": "completed"
        }
    },
    {
        "id": 2,
        "eventName": "User Registration",
        "timestamp": "2024-01-15T10:30:00Z",
        "actionType": "HTTP",
        "payload": {
            "userId": "user_123",
            "email": "test@example.com",
            "status": "active"
        }
    },
    {
        "id": 3,
        "eventName": "Order Shipped",
        "timestamp": "2024-01-15T14:20:00Z",
        "actionType": "KAFKA",
        "payload": {
            "orderId": "ord_456",
            "shippingMethod": "express",
            "trackingNumber": "TRK789012345",
            "estimatedDelivery": "2024-01-18"
        }
    }
]
```

### 2. Applying the Plugin to a Project

To use this plugin in another Gradle project, follow these steps:

**Step 1: Publish to Maven Local**

From the root directory of *this* (the plugin's) project, run:

```bash
./gradlew publishToMavenLocal
```

**Step 2: Configure the Target Project's Settings**

In your target project's `settings.gradle` file, add `mavenLocal()` to the `pluginManagement` block:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}
```

**Step 3: Apply the Plugin**

In your target project's `build.gradle` file, apply and configure the plugin:

```kotlin
plugins {
    id("com.komissarov.demo") version "1.0.0"
}

demo {
    file {
        chunkSize = Math.max(Runtime.getRuntime().availableProcessors(), 64)
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

    concurrencyLimit = Math.max(Runtime.getRuntime().availableProcessors(), 64)
}
```

### 3. Event Schema Validation (Optional)

To ensure your events file is structurally correct before processing, the plugin provides the `validateSchema` task. It
automatically checks if your `events.json` (or the file specified in the configuration) conforms to the required
internal schema.

If any issues are found — for example, a mandatory field like `id` is missing, or `actionType` has an invalid value —
the task will fail with a clear message detailing what's wrong. This helps catch typos and structural errors early in
your workflow.

## 📁 Examples

The [`examples/`](examples) directory contains preconfigured Gradle projects that demonstrate how to integrate and use
the Demo plugin in real-world scenarios.

## 🚀 Running the Tasks

The plugin adds two Gradle tasks to your project:

* **`validateSchema`**: Validates the structure of your JSON events file against the expected schema. Useful to run
  before the main processing.
  ```bash
  ./gradlew validateSchema
  ```
* **`processEvents`**: Reads the events file and executes the corresponding action (DB, HTTP, or Kafka) for each event.
  ```bash
  ./gradlew processEvents
  ```

## 🧪 Testing the Plugin

To run the tests for *this* plugin itself, execute the following in the plugin's root directory:

```bash
./gradlew test
```

## 🛠 Tech Stack

* **Language:** [Kotlin](https://kotlinlang.org/)
* **Build Tool:** [Gradle](https://gradle.com/)
* **Database:** [Exposed](https://github.com/JetBrains/Exposed)
* **HTTP Client:** [Ktor Client](https://ktor.io/docs/client-create-new-application.html)
* **Messaging:** [Apache Kafka Clients](https://kafka.apache.org/documentation.html)
* **Dependency Injection:** [Koin](https://insert-koin.io/)
* **Logging:** [Tinylog](https://www.tinylog.org/)
* **Testing:**
    * [JUnit 5](https://junit.org/junit5/)
    * [Cucumber](https://cucumber.io/)
    * [MockK](https://mockk.io/)
    * [AssertJ](https://assertj.github.io/doc/)
    * [DataFaker](https://www.datafaker.net/)
* **Code Quality:** [Ktlint](https://pinterest.github.io/ktlint/)
* **CI/CD:** [GitHub Actions](https://github.com/features/actions)

## 👨‍💻 About Me

Hi, I'm Peter!

* 🔭 I’m currently working on **Cloud TMS**.
* 👯 I’m looking to collaborate on **Backend and QA** projects.

## 🌐 Socials

[![LinkedIn](https://img.shields.io/badge/LinkedIn-%230077B5.svg?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/komissarovp/)

## 📊 GitHub Stats

[![GitHub Streak](https://streak-stats.demolab.com?user=petr-komissarov&theme=material-palenight&short_numbers=true&date_format=j%20M%5B%20Y%5D)](https://git.io/streak-stats)

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.
