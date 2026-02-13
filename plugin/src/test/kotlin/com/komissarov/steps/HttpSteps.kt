package com.komissarov.steps

import com.komissarov.services.Http
import com.komissarov.shared.Data
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals

internal class HttpSteps {
    private val mock = mockk<Http>()
    private var actual: Int? = -1

    @Given("mock http.post with value {int}")
    fun mockHttpPostWithValue(value: Int) {
        every { runBlocking { mock.post(Data.payload) } } returns value
    }

    @When("run http.post")
    fun runHttpPost() {
        actual = runBlocking { mock.post(Data.payload) }
    }

    @Then("assert http.post return value {int}")
    fun assertHttpPostReturnValue(expected: Int) {
        assertEquals(expected, actual)
    }
}
