package ru.stroganov.account.userauthservicek.repo

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.JsonBody.json
import org.mockserver.model.MediaType
import org.mockserver.verify.VerificationTimes.once
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.*
import ru.stroganov.account.userauthservicek.config.AppConfig
import ru.stroganov.account.userauthservicek.repo.config.hydraClient
import sh.ory.hydra.model.AcceptLoginRequest
import sh.ory.hydra.model.LoginRequest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Testcontainers
internal class HydraAdminRepoImplTest {

    companion object {
        @Container
        private val mockServer = MockServerContainer(
            DockerImageName
                .parse("mockserver/mockserver")
                .withTag("mockserver-" + MockServerClient::class.java.`package`.implementationVersion)
        )
    }

    private val config: AppConfig.HydraClient by lazy {
        AppConfig.HydraClient(
            "http://${mockServer.host}:${mockServer.serverPort}",
            "test-default.com"
        )
    }

    private val hydraAdminRepo: HydraAdminRepo by lazy { HydraAdminRepoImpl(hydraClient(config)) }

    private val mockClient: MockServerClient by lazy {
        MockServerClient(mockServer.host, mockServer.serverPort)
    }

    @BeforeTest
    fun beforeTest() {
        mockClient.reset()
    }

    @Test
    fun `POSITIVE ~~ getLoginRequest`() {
        val input = "test-login-challenge"

        val mockResponse = LoginRequest().apply {
            challenge(input)
        }
        val expectations = mockClient.`when`(
            request("/oauth2/auth/requests/login").apply {
                withMethod("GET")
                withQueryStringParameter("login_challenge", input)
            }
        ).respond(
            response().apply {
                withBody(json(mockResponse))
            }
        )

        val expected = LoginRequestResponse(input)
        val actual = runBlocking { hydraAdminRepo.getLoginRequest(input) }
        assertEquals(expected, actual)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `NEGATIVE ~~ getLoginRequest ~~ request is not successful`() {
        val input = "test-login-challenge"

        val expectations = mockClient.`when`(
            request("/oauth2/auth/requests/login").apply {
                withMethod("GET")
                withQueryStringParameter("login_challenge", input)
            }
        ).respond(
            response().apply {
                withStatusCode(500)
            }
        )

        val actual = assertThrows<GetLoginRequestException> {
            runBlocking { hydraAdminRepo.getLoginRequest(input) }
        }
        assertEquals(input, actual.loginRequestId)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `POSITIVE ~~ acceptLogin`() {
        val loginChallenge = "test-login-challenge"
        val subject = "test-subject"

        val expectations = mockClient.`when`(
            request("/oauth2/auth/requests/login/accept").apply {
                withMethod("PUT")
                withQueryStringParameter("login_challenge", loginChallenge)
                withContentType(MediaType.APPLICATION_JSON_UTF_8)
                withBody(
                    json(
                        AcceptLoginRequest().apply {
                            subject(subject)
                        }
                    )
                )
            }
        ).respond(
            response().apply {
                withBody(json("{\"redirect_to\": \"test-redirect.com\"}"))
            }
        )

        val expected = AcceptLoginRepoResponse("test-redirect.com")
        val actual = runBlocking { hydraAdminRepo.acceptLogin(loginChallenge, subject) }
        assertEquals(expected, actual)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `NEGATIVE ~~ acceptLogin ~~ request is not successful`() {
        val loginChallenge = "test-login-challenge"
        val subject = "test-subject"

        val expectations = mockClient.`when`(
            request("/oauth2/auth/requests/login/accept").apply {
                withMethod("PUT")
                withQueryStringParameter("login_challenge", loginChallenge)
                withContentType(MediaType.APPLICATION_JSON_UTF_8)
                withBody(
                    json(
                        AcceptLoginRequest().apply {
                            subject(subject)
                        }
                    )
                )
            }
        ).respond(
            response().apply {
                withStatusCode(500)
            }
        )

        val actual = assertThrows<AcceptLoginException> {
            runBlocking { hydraAdminRepo.acceptLogin(loginChallenge, subject) }
        }
        assertEquals(loginChallenge, actual.loginRequestId)
        assertEquals(subject, actual.subject)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `POSITIVE ~~ getConsent`() {
        val consentChallenge = "test-login-challenge"

        val expectations = mockClient.`when`(
            request("/oauth2/auth/requests/consent").apply {
                withMethod("GET")
                withQueryStringParameter("consent_challenge", consentChallenge)
            }
        ).respond(
            response().apply {
                withBody(
                    json(
                        "{" +
                            "\"requested_access_token_audience\": [\"test-requestedAccessTokenAudience\"]," +
                            "\"requested_scope\": [\"test-requestScope\"]," +
                            "\"subject\": \"test-subject\"," +
                            "}"
                    )
                )
            }
        )

        val expected = GetConsentRepoResponse(
            listOf("test-requestedAccessTokenAudience"),
            listOf("test-requestScope"),
            "test-subject"
        )
        val actual = runBlocking { hydraAdminRepo.getConsent(consentChallenge) }
        assertEquals(expected, actual)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `NEGATIVE ~~ getConsent ~~ request is not successful`() {
        val consentChallenge = "test-login-challenge"

        val expectations = mockClient.`when`(
            request("/oauth2/auth/requests/consent").apply {
                withMethod("GET")
                withQueryStringParameter("consent_challenge", consentChallenge)
            }
        ).respond(
            response().apply {
                withStatusCode(500)
            }
        )

        val actual = assertThrows<GetConsentException> {
            runBlocking { hydraAdminRepo.getConsent(consentChallenge) }
        }
        assertEquals(consentChallenge, actual.consentChallenge)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `POSITIVE ~~ acceptConsent`() {
        val consentChallenge = "test-login-challenge"
        val scopes = listOf("test:scope")

        val expectations = mockClient.`when`(
            request("/oauth2/auth/requests/consent/accept").apply {
                withMethod("PUT")
                withQueryStringParameter("consent_challenge", consentChallenge)
                withContentType(MediaType.APPLICATION_JSON_UTF_8)
                withBody(json("{\"grant_scope\": [\"test:scope\"]}"))
            }
        ).respond(
            response().apply {
                withBody(json("{\"redirect_to\": \"test-redirect.com\"}"))
            }
        )

        val expected = AcceptConsentRepoResponse("test-redirect.com")
        val actual = runBlocking { hydraAdminRepo.acceptConsent(consentChallenge, scopes) }
        assertEquals(expected, actual)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `NEGATIVE ~~ acceptConsent ~~ request is not successful`() {
        val consentChallenge = "test-login-challenge"
        val scopes = listOf("test:scope")

        val expectations = mockClient.`when`(
            request("/oauth2/auth/requests/consent/accept").apply {
                withMethod("PUT")
                withQueryStringParameter("consent_challenge", consentChallenge)
                withContentType(MediaType.APPLICATION_JSON_UTF_8)
                withBody(json("{\"grant_scope\": [\"test:scope\"]}"))
            }
        ).respond(
            response().apply {
                withStatusCode(500)
            }
        )

        val actual = assertThrows<AcceptConsentException> {
            runBlocking { hydraAdminRepo.acceptConsent(consentChallenge, scopes) }
        }
        assertEquals(consentChallenge, actual.consentChallenge)
        assertEquals(scopes, actual.scopes)
        mockClient.verify(expectations.first().id, once())
    }
}
