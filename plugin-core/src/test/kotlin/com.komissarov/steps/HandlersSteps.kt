package com.komissarov.steps

import com.komissarov.base.Handler
import com.komissarov.data.dto.events.Payload
import com.komissarov.handlers.DBHandler
import com.komissarov.handlers.HttpHandler
import com.komissarov.handlers.KafkaHandler
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

internal class HandlersSteps {
    private lateinit var handler: Handler<Payload>
    private lateinit var payload: Payload

    @Given("dbHandler.processEvent is mocked")
    internal fun dbHandlerProcessEventIsMocked() {
        handler = mockk<DBHandler>()
        payload = mockk<Payload.DB>()

        coEvery { handler.processEvent(payload) } coAnswers {}
    }

    @Given("httpHandler.processEvent is mocked")
    internal fun httpHandlerProcessEventIsMocked() {
        handler = mockk<HttpHandler>()
        payload = mockk<Payload.Http>()

        coEvery { handler.processEvent(payload) } coAnswers { }
    }

    @Given("kafkaHandler.processEvent is mocked")
    internal fun kafkaHandlerProcessEventIsMocked() {
        handler = mockk<KafkaHandler>()
        payload = mockk<Payload.Kafka>()

        coEvery { handler.processEvent(payload) } coAnswers { }
    }

    @When("run processEvent")
    internal fun runProcessEvent() {
        runBlocking { handler.processEvent(payload) }
    }

    @Then("processEvent should be called {int} times")
    internal fun processEventShouldBeCalledNTimes(expected: Int) {
        coVerify(exactly = expected) { handler.processEvent(payload) }
        confirmVerified(handler, clear = true)
    }
}
