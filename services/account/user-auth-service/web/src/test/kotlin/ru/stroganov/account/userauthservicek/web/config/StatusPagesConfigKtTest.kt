package ru.stroganov.account.userauthservice.web.config

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import ru.stroganov.account.userauthservice.web.v1.createClient
import java.lang.RuntimeException
import kotlin.test.*

internal class StatusPagesConfigKtTest {

    private val responseMock: () -> String = mockk()

    private fun test(block: suspend ApplicationTestBuilder.() -> Unit) =
        ru.stroganov.account.userauthservice.web.v1.test({
            routing {
                get("/") {
                    call.respondText(responseMock())
                }
            }
        }) { block() }

    @Test
    fun `POSITIVE ~~ statusPagesConfig ~~ no errors`() = test {
        val client = createClient()

        coEvery { responseMock.invoke() } returns "test-response"

        val expected = "test-response"
        val actual = client.get("/")
        assertEquals(HttpStatusCode.OK, actual.status)
        assertEquals(expected, actual.bodyAsText())
    }

    @Test
    fun `NEGATIVE ~~ statusPagesConfig ~~ exception thrown`() = test {
        val client = createClient()

        coEvery { responseMock.invoke() } throws RuntimeException("test-response")

        val expected = "test-response"
        val actual = client.get("/")
        assertEquals(HttpStatusCode.InternalServerError, actual.status)
        assertEquals(expected, actual.bodyAsText())
    }
}
