package ru.stroganov.account.userauthservice.web.v1

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import ru.stroganov.account.userauthservice.common.UserId
import ru.stroganov.account.userauthservice.service.RegistrationNewServiceRequest
import ru.stroganov.account.userauthservice.service.RegistrationService
import kotlin.test.*

internal class RegistrationKtTest {

    private fun test(block: suspend ApplicationTestBuilder.() -> Unit) = test({
        registration(registrationService)
    }) { block() }

    private val registrationService: RegistrationService = mockk()

    @Test
    fun `POSITIVE ~~ POST ~~ registration`() = test {
        val client = createClient()

        val input = RegistrationNewRequest(
            "test-login",
            "test-password",
            "test-name"
        )

        coEvery { registrationService.new(any()) } returns UserId(1)

        val expected = RegistrationNewResponse(1)
        val actual = client.post("/registration/new") {
            contentType(ContentType.Application.Json)
            setBody(input)
        }
        assertEquals(HttpStatusCode.OK, actual.status)
        assertEquals(expected, actual.body())
        coVerify(exactly = 1) {
            registrationService.new(
                RegistrationNewServiceRequest(
                    "test-login",
                    "test-password",
                    "test-name"
                )
            )
        }
        confirmVerified(registrationService)
    }
}
