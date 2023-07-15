package ru.stroganov.account.userauthservicek.repo.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockserver.client.MockServerClient
import org.mockserver.model.*
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.JsonBody.json
import org.mockserver.model.Parameter.param
import org.mockserver.model.ParameterBody.params
import org.mockserver.verify.VerificationTimes.once
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import ru.stroganov.account.userauthservicek.config.AppConfig
import kotlin.test.*

@Testcontainers
class HttpClientKtTest {

    companion object {
        @Container
        val mockServer = MockServerContainer(
            DockerImageName
                .parse("mockserver/mockserver")
                .withTag("mockserver-" + MockServerClient::class.java.`package`.implementationVersion)
        )
    }

    private val mockClient: MockServerClient by lazy {
        MockServerClient(mockServer.host, mockServer.serverPort)
    }

    private val config: AppConfig.Oauth2Client by lazy {
        AppConfig.Oauth2Client(
            "clientId",
            "clientSecret",
            "http://${mockServer.host}:${mockServer.serverPort}",
            listOf("test:scope")
        )
    }

    @BeforeTest
    fun beforeTest() {
        mockClient.reset()
    }

    @Test
    fun `POSITIVE ~~ httpClient ~~ just success`() {
        val client = httpClient(config)

        val successExpectations = mockClient.`when`(
            request("/test").apply {
                withMethod("GET")
            }
        ).respond(
            response().apply {
                withStatusCode(200)
                withBody("test-success")
            }
        )

        val expected = "test-success"
        val actual = runBlocking { client.get("${config.publicUrl}/test").bodyAsText() }
        assertEquals(expected, actual)
        mockClient.verify(successExpectations.first().id, once())
    }

    @Test
    fun `POSITIVE ~~ httpClient ~~ oauth2token requested`() {
        val client = httpClient(config)

        val successExpectations = mockClient.`when`(
            request("/test").apply {
                withMethod("GET")
                withHeader("Authorization", "Bearer test-token")
            }
        ).respond(
            response().apply {
                withStatusCode(200)
                withBody("test-success")
            }
        )
        val deniedExpectations = mockClient.`when`(
            request("/test").apply {
                withMethod("GET")
            }
        ).respond(
            response().apply {
                withStatusCode(401)
            }
        )

        val tokenInfo = TokenInfo(
            accessToken = "test-token",
            expiresIn = 1,
            scope = config.scopes.joinToString(" "),
            tokenType = "accessToken"
        )
        val oauthExpectations = mockClient.`when`(
            request("/oauth2/token").apply {
                withMethod("POST")
                withContentType(MediaType.APPLICATION_FORM_URLENCODED.withCharset(Charsets.UTF_8))
                withBody(
                    params(
                        param("grant_type", "client_credentials"),
                        param("client_id", config.clientId),
                        param("client_secret", config.clientSecret),
                        param("scope", config.scopes)
                    )
                )
            }
        ).respond(
            response().apply {
                withStatusCode(200)
                withBody(json(Json.encodeToString(tokenInfo)))
            }
        )

        val expected = "test-success"
        val actual = runBlocking { client.get("${config.publicUrl}/test").bodyAsText() }
        assertEquals(expected, actual)
        mockClient.verify(deniedExpectations.first().id, once())
        mockClient.verify(oauthExpectations.first().id, once())
        mockClient.verify(successExpectations.first().id, once())
    }

    @Test
    fun `NEGATIVE ~~ httpClient ~~ oauth2token requested ~~ still 401`() {
        val client = httpClient(config)

        val successExpectations = mockClient.`when`(
            request("/test").apply {
                withMethod("GET")
                withHeader("Authorization", "Bearer test-token")
            }
        ).respond(
            response().apply {
                withStatusCode(401)
                withBody("test-success")
            }
        )
        val deniedExpectations = mockClient.`when`(
            request("/test").apply {
                withMethod("GET")
            }
        ).respond(
            response().apply {
                withStatusCode(401)
            }
        )

        val tokenInfo = TokenInfo(
            accessToken = "test-token",
            expiresIn = 1,
            scope = config.scopes.joinToString(" "),
            tokenType = "accessToken"
        )
        val oauthExpectations = mockClient.`when`(
            request("/oauth2/token").apply {
                withMethod("POST")
                withContentType(MediaType.APPLICATION_FORM_URLENCODED.withCharset(Charsets.UTF_8))
                withBody(
                    params(
                        param("grant_type", "client_credentials"),
                        param("client_id", config.clientId),
                        param("client_secret", config.clientSecret),
                        param("scope", config.scopes)
                    )
                )
            }
        ).respond(
            response().apply {
                withStatusCode(200)
                withBody(json(Json.encodeToString(tokenInfo)))
            }
        )

        val expected = 401
        val actual = assertThrows<ClientRequestException> {
            runBlocking { client.get("${config.publicUrl}/test").bodyAsText() }
        }
        assertEquals(expected, actual.response.status.value)
        mockClient.verify(deniedExpectations.first().id, once())
        mockClient.verify(oauthExpectations.first().id, once())
        mockClient.verify(successExpectations.first().id, once())
    }

    @ParameterizedTest
    @ValueSource(ints = [300, 350, 399])
    fun `NEGATIVE ~~ httpClient ~~ 3xx return`(code: Int) {
        val client = httpClient(config)

        val successExpectations = mockClient.`when`(
            request("/test").apply {
                withMethod("GET")
            }
        ).respond(
            response().apply {
                withStatusCode(code)
            }
        )

        val actual = assertThrows<RedirectResponseException> {
            runBlocking { client.get("${config.publicUrl}/test").bodyAsText() }
        }
        assertEquals(code, actual.response.status.value)
        mockClient.verify(successExpectations.first().id, once())
    }

    /**
     * 401 is a bit different because that triggers oauth flow
     */
    @ParameterizedTest
    @ValueSource(ints = [400, 403, 404, 450, 499])
    fun `NEGATIVE ~~ httpClient ~~ 4xx return`(code: Int) {
        val client = httpClient(config)

        val successExpectations = mockClient.`when`(
            request("/test").apply {
                withMethod("GET")
            }
        ).respond(
            response().apply {
                withStatusCode(code)
            }
        )

        val actual = assertThrows<ClientRequestException> {
            runBlocking { client.get("${config.publicUrl}/test").bodyAsText() }
        }
        assertEquals(code, actual.response.status.value)
        mockClient.verify(successExpectations.first().id, once())
    }

    @ParameterizedTest
    @ValueSource(ints = [500, 503, 550])
    fun `NEGATIVE ~~ httpClient ~~ 5xx return`(code: Int) {
        val client = httpClient(config)

        val successExpectations = mockClient.`when`(
            request("/test").apply {
                withMethod("GET")
            }
        ).respond(
            response().apply {
                withStatusCode(code)
            }
        )

        val actual = assertThrows<ServerResponseException> {
            runBlocking { client.get("${config.publicUrl}/test").bodyAsText() }
        }
        assertEquals(code, actual.response.status.value)
        mockClient.verify(successExpectations.first().id, once())
    }
}
