package com.komissarov.steps

import com.komissarov.Settings
import com.komissarov.models.Event
import com.komissarov.services.DB
import com.komissarov.services.Http
import com.komissarov.services.Kafka
import com.komissarov.shared.Data
import com.komissarov.tools.Handler
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals

internal class HandlerSteps {
    companion object {
        private val httpEvent by lazy {
            Event(
                id = Data.faker.number().positive(),
                eventName = Data.faker.text().text(16),
                timestamp = Data.faker.timeAndDate().past().toString(),
                actionType = Settings.ActionTypes.HTTP,
                payload = Data.payload
            )
        }

        private val dbEvent by lazy {
            Event(
                id = Data.faker.number().positive(),
                eventName = Data.faker.text().text(16),
                timestamp = Data.faker.timeAndDate().past().toString(),
                actionType = Settings.ActionTypes.DB,
                payload = Data.payload
            )
        }

        private val kafkaEvent by lazy {
            Event(
                id = Data.faker.number().positive(),
                eventName = Data.faker.text().text(16),
                timestamp = Data.faker.timeAndDate().past().toString(),
                actionType = Settings.ActionTypes.KAFKA,
                payload = Data.payload
            )
        }
    }

    private lateinit var handler: Handler
    private var actual: Int? = -1

    @Given("mock services with values:")
    fun mockServicesWithValues(table: Map<String, Int>) {
        val http = mockk<Http>()
        val db = mockk<DB>()
        val kafka = mockk<Kafka>()

        every { runBlocking { http.post(Data.payload) } } returns table.getValue(Settings.ActionTypes.HTTP)
        every { runBlocking { db.update(Data.payload) } } returns table.getValue(Settings.ActionTypes.DB)
        every { runBlocking { kafka.send(Data.payload) } } returns table.getValue(Settings.ActionTypes.KAFKA)

        handler = Handler(http, db, kafka)
    }

    @When("run handler.processEvent with actionType {string}")
    fun runHandlerProcessEventWithActionType(actionType: String) {
        val event = when (actionType) {
            Settings.ActionTypes.HTTP -> {
                httpEvent
            }

            Settings.ActionTypes.DB -> {
                dbEvent
            }

            Settings.ActionTypes.KAFKA -> {
                kafkaEvent
            }

            else -> {
                mockk<Event>()
            }
        }

        actual = runBlocking { handler.processEvent(event) }
    }

    @Then("assert handler.processEvent return value {int}")
    fun assertHandlerProcessEventReturnValue(expected: Int) {
        assertEquals(expected, actual)
    }
}
