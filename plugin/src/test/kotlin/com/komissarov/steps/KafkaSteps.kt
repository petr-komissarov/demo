package com.komissarov.steps

import com.komissarov.services.Kafka
import com.komissarov.shared.Data
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals

internal class KafkaSteps {
    private val mock = mockk<Kafka>()
    private var actual: Int? = -1

    @Given("mock kafka.send with value {int}")
    fun mockKafkaSendWithValue(value: Int) {
        every { runBlocking { mock.send(Data.payload) } } returns value
    }

    @When("run kafka.send")
    fun runKafkaSend() {
        actual = runBlocking { mock.send(Data.payload) }
    }

    @Then("assert kafka.send return value {int}")
    fun assertKafkaSendReturnValue(expected: Int) {
        assertEquals(expected, actual)
    }
}
