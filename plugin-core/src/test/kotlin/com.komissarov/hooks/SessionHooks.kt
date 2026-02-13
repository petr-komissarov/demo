package com.komissarov.hooks

import io.cucumber.java.AfterAll
import io.cucumber.java.BeforeAll
import org.tinylog.Logger

@BeforeAll
@Suppress("UNUSED")
internal fun beforeAll() {
    Logger.info { "✅ Before all hook" }
}

@AfterAll
@Suppress("UNUSED")
internal fun afterAll() {
    Logger.info { "✅ After all hook" }
}
