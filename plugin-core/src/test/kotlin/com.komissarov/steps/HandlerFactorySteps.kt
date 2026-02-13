package com.komissarov.steps

import com.komissarov.HandlerFactory
import com.komissarov.base.Handler
import com.komissarov.data.dto.events.Payload
import com.komissarov.data.enums.ActionType
import com.komissarov.handlers.DBHandler
import com.komissarov.handlers.HttpHandler
import com.komissarov.handlers.KafkaHandler
import io.cucumber.java.ParameterType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat

internal class HandlerFactorySteps {
    private lateinit var actual: Handler<Payload>
    private lateinit var handlerFactory: HandlerFactory
    private lateinit var handlers: Map<ActionType, Handler<Payload>>

    @ParameterType("[^\\s]+")
    fun actionType(actionName: String): ActionType = ActionType.valueOf(actionName)

    @Given("handlers are mocked")
    internal fun handlersAreMocked() {
        handlers = mapOf(
            ActionType.DB to mockk<DBHandler>(),
            ActionType.HTTP to mockk<HttpHandler>(),
            ActionType.KAFKA to mockk<KafkaHandler>()
        )
    }

    @Given("handlerFactory is mocked")
    internal fun handlerFactoryIsMocked() {
        handlerFactory = spyk(
            HandlerFactory(
                handlers.getValue(ActionType.DB) as DBHandler,
                handlers.getValue(ActionType.HTTP) as HttpHandler,
                handlers.getValue(ActionType.KAFKA) as KafkaHandler
            )
        )
    }

    @When("run handlerFactory.getHandler with actionType {actionType}")
    internal fun runHandlerFactoryGetHandlerWithActionType(actionType: ActionType) {
        actual = handlerFactory.getHandler(actionType) ?: mockk<Handler<Payload>>()
    }

    @Then("handlerFactory.getHandler should return {actionType} handler")
    internal fun handlerFactoryGetHandlerShouldReturnHandler(actionType: ActionType) {
        assertThat(actual).isEqualTo(handlers[actionType])
    }

    @Then("handlerFactory.getHandler should be called {int} times")
    internal fun handlerFactoryGetHandlerShouldBeCalledNTimes(expected: Int) {
        verify(exactly = expected) { handlerFactory.getHandler(allAny()) }
        confirmVerified(handlerFactory, clear = true)
    }
}
