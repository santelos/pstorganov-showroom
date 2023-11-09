package ru.stroganov.account.userauthservicek.repo

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.assertThrows
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody.json
import org.mockserver.verify.VerificationTimes.once
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import ru.stroganov.account.userauthservice.common.BaseException.RepoException.*
import ru.stroganov.account.userauthservice.config.AppConfig
import kotlin.test.*

@Testcontainers
internal class UserInfoServiceRepoImplTest {

    companion object {
        @Container
        val mockServer = MockServerContainer(
            DockerImageName
                .parse("mockserver/mockserver")
                .withTag("mockserver-" + MockServerClient::class.java.`package`.implementationVersion)
        )
    }

    private val httpClient: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) { json() }
            install(DefaultRequest) {
                url("http://${mockServer.host}:${mockServer.serverPort}")
            }
        }
    }

    private val userInfoServiceRepo: UserInfoServiceRepo by lazy {
        UserInfoServiceRepoImpl(httpClient)
    }
    private val mockClient: MockServerClient by lazy {
        MockServerClient(mockServer.host, mockServer.serverPort)
    }

    @BeforeTest
    fun beforeTest() {
        mockClient.reset()
    }

    @Test
    fun `POSITIVE ~~ createUser`() {
        val request = CreateUserRepoRequest(
            "test-login",
            "test-password",
            "test-name"
        )

        val mockResponse = CreateUserRepoResponse(1)
        mockClient.`when`(
            HttpRequest.request("/v1/user").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }
        ).respond(
            HttpResponse.response().apply {
                withBody(json(Json.encodeToString(mockResponse)))
            }
        )

        val expected = CreateUserRepoResponse(1)
        val actual = runBlocking { userInfoServiceRepo.createUser(request) }
        assertEquals(expected, actual)
    }

    @Test
    fun `NEGATIVE ~~ createUser ~~ request is not successful`() {
        val request = CreateUserRepoRequest(
            "test-login",
            "test-password",
            "test-name"
        )

        val expectations = mockClient.`when`(
            HttpRequest.request("/v1/user").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }
        ).respond(
            HttpResponse.response().apply {
                withStatusCode(500)
            }
        )

        assertThrows<HttpClientException> {
            runBlocking { userInfoServiceRepo.createUser(request) }
        }
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `POSITIVE ~~ getUserIdInfo`() {
        val request = GetUserIdRepoRequest("test--login")

        val mockResponse = GetUserIdRepoResponse(1)
        val expectations = mockClient.`when`(
            HttpRequest.request("/v1/user/id").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }
        ).respond(
            HttpResponse.response().apply {
                withBody(json(Json.encodeToString(mockResponse)))
            }
        )

        val expected = GetUserIdRepoResponse(1)
        val actual = runBlocking { userInfoServiceRepo.getUserId(request) }
        assertEquals(expected, actual)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `NEGATIVE ~~ getUserIdInfo`() {
        val request = GetUserIdRepoRequest("test--login")

        val expectations = mockClient.`when`(
            HttpRequest.request("/v1/user/id").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }
        ).respond(
            HttpResponse.response().apply {
                withStatusCode(500)
            }
        )

        assertThrows<HttpClientException> {
            runBlocking { userInfoServiceRepo.getUserId(request) }
        }
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `POSITIVE ~~ getUserAuthInfo`() {
        val request = GetUserAuthInfoRepoRequest(1)
        val passwordHash = "test--password-hash"
        val roles = setOf("test:role")

        val mockResponse = GetUserAuthInfoRepoResponse(passwordHash, roles)
        val expectations = mockClient.`when`(
            HttpRequest.request("/v1/user/auth-info").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }
        ).respond(
            HttpResponse.response().apply {
                withBody(json(Json.encodeToString(mockResponse)))
            }
        )

        val expected = GetUserAuthInfoRepoResponse(passwordHash, roles)
        val actual = runBlocking { userInfoServiceRepo.getUserAuthInfo(request) }
        assertEquals(expected, actual)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `NEGATIVE ~~ getUserAuthInfo`() {
        val request = GetUserAuthInfoRepoRequest(1)

        val expectations = mockClient.`when`(
            HttpRequest.request("/v1/user/auth-info").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }
        ).respond(
            HttpResponse.response().apply {
                withStatusCode(500)
            }
        )

        assertThrows<HttpClientException> {
            runBlocking { userInfoServiceRepo.getUserAuthInfo(request) }
        }
        mockClient.verify(expectations.first().id, once())
    }
}
