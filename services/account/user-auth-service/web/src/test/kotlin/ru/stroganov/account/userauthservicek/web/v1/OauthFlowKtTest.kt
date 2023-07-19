package ru.stroganov.account.userauthservice.web.v1

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import ru.stroganov.account.userauthservice.common.UserId
import ru.stroganov.account.userauthservice.service.ConsentService
import ru.stroganov.account.userauthservice.service.GetConsentResponse
import ru.stroganov.account.userauthservice.service.LoginService
import ru.stroganov.account.userauthservice.web.config.BASIC_AUTH
import kotlin.test.*

internal class OauthFlowKtTest {

    private val loginService: LoginService = mockk()
    private val consentService: ConsentService = mockk()
    private fun test(block: suspend ApplicationTestBuilder.() -> Unit) = test({
        testAuth()
        oauthFlow(loginService, consentService)
    }) { block() }

    private val authMock: () -> Principal = mockk()
    private fun Application.testAuth() {
        install(Authentication) {
            basic(BASIC_AUTH) {
                validate {
                    authMock()
                }
            }
        }
    }

    @Test
    fun `POSITIVE ~~ GET ~~ accept-login`() = test {
        val client = createClient()

        val loginChallenge = "test-login-challenge"
        val userId = UserId(1)

        coEvery { authMock.invoke() } returns UserIdPrincipal("1")
        val redirectUrl = "test-redirect-url"
        coEvery { loginService.acceptLogin(any(), any()) } returns redirectUrl

        val expected = AcceptLoginWebResponse(redirectUrl)
        val actual = client.get("/accept-login") {
            basicAuth("", "")
            url {
                parameters.append("login_challenge", loginChallenge)
            }
        }
        assertEquals(HttpStatusCode.OK, actual.status)
        assertEquals(expected, actual.body())
        coVerify(exactly = 1) { loginService.acceptLogin(userId, loginChallenge) }
        confirmVerified(loginService)
    }

    @Test
    fun `POSITIVE ~~ GET ~~ accept-consent`() = test {
        val client = createClient()

        val consentChallenge = "test-consent-challenge"

        coEvery { authMock.invoke() } returns UserIdPrincipal("1")
        val redirectUrl = "test-redirect-url"
        coEvery { consentService.acceptConsent(any(), any()) } returns redirectUrl

        val expected = AcceptConsentWebResponse(redirectUrl)
        val actual = client.get("/accept-consent") {
            basicAuth("", "")
            url {
                parameters.append("consent_challenge", consentChallenge)
            }
        }
        assertEquals(HttpStatusCode.OK, actual.status)
        assertEquals(expected, actual.body())
        coVerify(exactly = 1) { consentService.acceptConsent(consentChallenge, emptyList()) }
        confirmVerified(consentService)
    }

    @Test
    fun `POSITIVE ~~ POST ~~ accept-consent`() = test {
        val client = createClient()

        val input = AcceptConsentPostWebRequest(listOf("test:scope"))
        val consentChallenge = "test-consent-challenge"

        coEvery { authMock.invoke() } returns UserIdPrincipal("1")
        val redirectUrl = "test-redirect-url"
        coEvery { consentService.acceptConsent(any(), any()) } returns redirectUrl

        val expected = AcceptConsentWebResponse(redirectUrl)
        val actual = client.post("/accept-consent") {
            basicAuth("", "")
            url {
                parameters.append("consent_challenge", consentChallenge)
            }
            contentType(ContentType.Application.Json)
            setBody(input)
        }
        assertEquals(HttpStatusCode.OK, actual.status)
        assertEquals(expected, actual.body())
        coVerify(exactly = 1) { consentService.acceptConsent(consentChallenge, listOf("test:scope")) }
        confirmVerified(consentService)
    }

    @Test
    fun `POSITIVE ~~ GET ~~ get-consent`() = test {
        val client = createClient()

        val consentChallenge = "test-consent-challenge"

        coEvery { authMock.invoke() } returns UserIdPrincipal("1")
        val consentResponse = GetConsentResponse(
            listOf("test-token-audience"),
            listOf("test-request-scope"),
            "test-subject"
        )
        coEvery { consentService.getConsent(any()) } returns consentResponse

        val expected = GetConsentWebResponse(
            listOf("test-token-audience"),
            listOf("test-request-scope"),
            "test-subject"
        )
        val actual = client.get("/get-consent") {
            basicAuth("", "")
            url {
                parameters.append("consent_challenge", consentChallenge)
            }
        }
        assertEquals(HttpStatusCode.OK, actual.status)
        assertEquals(expected, actual.body())
        coVerify(exactly = 1) { consentService.getConsent(consentChallenge) }
        confirmVerified(consentService)
    }
}
