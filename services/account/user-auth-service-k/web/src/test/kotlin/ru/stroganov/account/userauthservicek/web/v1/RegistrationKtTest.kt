package ru.stroganov.account.userauthservicek.web.v1

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import ru.stroganov.account.userauthservicek.common.UserId
import ru.stroganov.account.userauthservicek.service.RegistrationNewServiceRequest
import ru.stroganov.account.userauthservicek.service.RegistrationService
import kotlin.test.*

internal class RegistrationKtTest {

    private val registrationService: RegistrationService = mockk()
    private fun test(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            registration(registrationService)
        }
        block()
    }

    @Test
    fun `POSITIVE ~~ registration`() = test {
        val input = RegistrationNewRequest(
            "test-login",
            "test-password",
            "test-name"
        )

        coEvery { registrationService.new(any()) } returns UserId(1)

        val expected = RegistrationNewResponse(1)
        val actual = client.post("/registration/new") {
            setBody(input)
        }
        assertEquals(HttpStatusCode.OK, actual.status)
        assertEquals(expected, actual.body())
        coVerify(exactly = 1) { registrationService.new(RegistrationNewServiceRequest(
            "test-login",
            "test-password",
            "test-name"
        )) }
        confirmVerified(registrationService)
    }

    @Test
    fun `NEGATIVE ~~ registration ~~ service exception`() = test {
        val input = RegistrationNewRequest(
            "test-login",
            "test-password",
            "test-name"
        )

        coEvery { registrationService.new(any()) } throws RuntimeException()

        val actual = client.post("/registration/new") {
            setBody(input)
        }
        assertEquals(HttpStatusCode.InternalServerError, actual.status)
        coVerify(exactly = 1) { registrationService.new(RegistrationNewServiceRequest(
            "test-login",
            "test-password",
            "test-name"
        )) }
        confirmVerified(registrationService)
    }
}
