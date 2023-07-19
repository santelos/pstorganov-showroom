package ru.stroganov.account.userauthservice.repo

import io.ktor.client.*
import io.ktor.client.engine.cio.*
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
import ru.stroganov.account.userauthservice.common.BaseException.RepoException.CreateUserException
import ru.stroganov.account.userauthservice.common.BaseException.RepoException.GetUserInfoException
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
        HttpClient(CIO) { install(ContentNegotiation) { json() } }
    }

    private val userInfoServiceRepo: UserInfoServiceRepo by lazy {
        UserInfoServiceRepoImpl(
            httpClient,
            AppConfig.UserInfoService("http://${mockServer.host}:${mockServer.serverPort}")
        )
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
        val request = UserInfoServiceRepoCreateUserRequest(
            "test-login",
            "test-password",
            "test-name"
        )

        val mockResponse = UserInfoServiceRepoCreateUserResponse(1)
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

        val expected = UserInfoServiceRepoCreateUserResponse(1)
        val actual = runBlocking { userInfoServiceRepo.createUser(request) }
        assertEquals(expected, actual)
    }

    @Test
    fun `Negative ~~ createUser ~~ request is not successful`() {
        val request = UserInfoServiceRepoCreateUserRequest(
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

        val actual = assertThrows<CreateUserException> {
            runBlocking { userInfoServiceRepo.createUser(request) }
        }
        assertEquals(request.login, actual.login)
        mockClient.verify(expectations.first().id, once())
    }

    @Test
    fun `POSITIVE ~~ getUserAuthInfo`() {
        val request = UserInfoServiceRepoGetUserAuthInfoRequest(
            "test-login",
            "test-password"
        )

        val mockResponse = UserInfoServiceRepoUserAuthInfoResponseInternal(
            true,
            emptyList(),
            1
        )
        mockClient.`when`(
            HttpRequest.request("/v1/user/auth-info").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }
        ).respond(
            HttpResponse.response().apply {
                withBody(json(Json.encodeToString(mockResponse)))
            }
        )

        val expected = UserInfoServiceRepoUserAuthInfoResponse.Success(1)
        val actual = runBlocking { userInfoServiceRepo.getUserAuthInfo(request) }
        assertEquals(expected, actual)
    }

    @Test
    fun `NEGATIVE ~~ getUserAuthInfo ~~ business downstream error`() {
        val request = UserInfoServiceRepoGetUserAuthInfoRequest(
            "test-login",
            "test-password"
        )

        val mockResponse = UserInfoServiceRepoUserAuthInfoResponseInternal(
            false,
            listOf("test-error"),
            0
        )
        mockClient.`when`(
            HttpRequest.request("/v1/user/auth-info").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }
        ).respond(
            HttpResponse.response().apply {
                withBody(json(Json.encodeToString(mockResponse)))
            }
        )

        val expected = UserInfoServiceRepoUserAuthInfoResponse.Invalid(listOf("test-error"))
        val actual = runBlocking { userInfoServiceRepo.getUserAuthInfo(request) }
        assertEquals(expected, actual)
    }

    @Test
    fun `NEGATIVE ~~ getUserAuthInfo ~~ request is not successful`() {
        val request = UserInfoServiceRepoGetUserAuthInfoRequest(
            "test-login",
            "test-password"
        )

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

        val actual = assertThrows<GetUserInfoException> {
            runBlocking { userInfoServiceRepo.getUserAuthInfo(request) }
        }
        assertEquals(request.login, actual.login)
        mockClient.verify(expectations.first().id, once())
    }
}
