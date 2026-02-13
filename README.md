![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-f5f5f5?style=for-the-badge&logo=junit5&logoColor=dc524a)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)

# 🚀 Demo

This Gradle plugin is for testing purposes only!<br>
The plugin is designed to handle events and provides flexible configuration via a JSON file

## 🎯 Stack

1. Cucumber - BDD framework
2. DataFaker - Fake data generator
3. Exposed - SQL library
4. Gradle - Build tool
5. Gson - Serialization/deserialization tool
6. JUnit5 - JVM testing framework
7. Kafka - Event streaming platform
8. Kotlin - Programming language
9. Mockk - Mocking framework for unit tests
10. OkHttp3 - HTTP client

## 🔗 Usage

The *runDemo* task performs an action depending on the actionType

1. HTTP - send an HTTP request
2. DB - execute an SQL query
3. KAFKA - send a message to the Kafka topic

Example *events.json*

```json
[
  {
    "id": 1,
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
    "id": 2,
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

## 🔑 Dev

Publish the plugin to a *local* Maven repository

```bash
./gradlew publishToMavenLocal
```

Insert the lines at the top of the *settings.gradle* file in any Gradle project

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
```

Then add the plugin

```kotlin
plugins {
    id("com.komissarov.demo") version ("1.0.0")
}

demo {
    relativePath = "events.json"
}
```

Run the *runDemo* task

```bash
./gradlew runDemo
```

## 💊 Test

```bash
./gradlew test
```

## 🎁 License

[MIT](./LICENSE)

## 💫 About me

🔭 I’m currently working on Cloud TMS<br>👯 I’m looking to collaborate on Backend, QA

## 📊 GitHub stats

![](https://github-readme-stats.vercel.app/api?username=petr-komissarov&theme=ocean_dark&hide_border=false&include_all_commits=false&count_private=false)<br/>
![](https://nirzak-streak-stats.vercel.app/?user=petr-komissarov&theme=ocean_dark&hide_border=false)<br/>
![](https://github-readme-stats.vercel.app/api/top-langs/?username=petr-komissarov&theme=ocean_dark&hide_border=false&include_all_commits=false&count_private=false&layout=compact)

## 🌐 Socials

[![LinkedIn](https://img.shields.io/badge/LinkedIn-%230077B5.svg?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/komissarovp/)
