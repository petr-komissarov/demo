package com.komissarov.steps

import com.komissarov.services.DB
import com.komissarov.shared.Data
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals

internal class DBSteps {
    private val mock = mockk<DB>()
    private var actual: Int? = -1

    @Given("mock db.update with value {int}")
    fun mockDBUpdateWithValue(value: Int) {
        every { runBlocking { mock.update(Data.payload) } } returns value
    }

    @When("run db.update")
    fun runDBUpdate() {
        actual = runBlocking { mock.update(Data.payload) }
    }

    @Then("assert db.update return value {int}")
    fun assertDBUpdateReturnValue(expected: Int) {
        assertEquals(expected, actual)
    }
}
