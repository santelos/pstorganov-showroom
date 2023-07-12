package ru.stroganov.account.userauthservicek.repo

import kotlin.test.*
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
import org.mockserver.model.JsonBody
import org.mockserver.model.JsonBody.json
import org.mockserver.verify.VerificationTimes.once
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import ru.stroganov.account.userauthservicek.common.BaseException
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.CreateUserException
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.GetUserInfoException
import ru.stroganov.account.userauthservicek.config.AppConfig

@Testcontainers
internal class UserInfoServiceRepoImplTest {

    @Container
    val mockServer = MockServerContainer(
        DockerImageName
        .parse("mockserver/mockserver")
        .withTag("mockserver-" + MockServerClient::class.java.`package`.implementationVersion))

    private val httpClient: HttpClient by lazy {
        HttpClient(CIO) { install(ContentNegotiation) { json() } }
    }

    private val userInfoServiceRepo: UserInfoServiceRepo by lazy {
        UserInfoServiceRepoImpl(
            httpClient,
            AppConfig.UserInfoService("http://${mockServer.host}:${mockServer.serverPort}")
        )
    }

    @Test
    fun `POSITIVE ~~ createUser`() {
        MockServerClient(mockServer.host, mockServer.serverPort).use { mockClient ->
            val request = UserInfoServiceRepoCreateUserRequest(
                "test-login",
                "test-password",
                "test-name"
            )

            val mockResponse = UserInfoServiceRepoCreateUserResponse(1)
            mockClient.`when`(HttpRequest.request("/v1/user").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }).respond(HttpResponse.response().apply {
                withBody(json(Json.encodeToString(mockResponse)))
            })

            val expected = UserInfoServiceRepoCreateUserResponse(1)
            val actual = runBlocking { userInfoServiceRepo.createUser(request) }
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Negative ~~ createUser ~~ request is not successful`() {
        MockServerClient(mockServer.host, mockServer.serverPort).use { mockClient ->
            val request = UserInfoServiceRepoCreateUserRequest(
                "test-login",
                "test-password",
                "test-name"
            )

            val expectations = mockClient.`when`(HttpRequest.request("/v1/user").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }).respond(HttpResponse.response().apply {
                withStatusCode(500)
            })

            val actual = assertThrows<CreateUserException> {
                runBlocking { userInfoServiceRepo.createUser(request) }
            }
            assertEquals(request.login, actual.login)
            mockClient.verify(expectations.first().id, once())
        }
    }

    @Test
    fun `POSITIVE ~~ getUserAuthInfo`() {
        MockServerClient(mockServer.host, mockServer.serverPort).use { mockClient ->
            val request = UserInfoServiceRepoGetUserAuthInfoRequest(
                "test-login",
                "test-password"
            )

            val mockResponse = UserInfoServiceRepoUserAuthInfoResponseInternal(
                true,
                emptyList(),
                1
            )
            mockClient.`when`(HttpRequest.request("/v1/user/auth-info").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }).respond(HttpResponse.response().apply {
                withBody(json(Json.encodeToString(mockResponse)))
            })

            val expected = UserInfoServiceRepoUserAuthInfoResponse.Success(1)
            val actual = runBlocking { userInfoServiceRepo.getUserAuthInfo(request) }
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `NEGATIVE ~~ getUserAuthInfo ~~ business downstream error`() {
        MockServerClient(mockServer.host, mockServer.serverPort).use { mockClient ->
            val request = UserInfoServiceRepoGetUserAuthInfoRequest(
                "test-login",
                "test-password"
            )

            val mockResponse = UserInfoServiceRepoUserAuthInfoResponseInternal(
                false,
                listOf("test-error"),
                0
            )
            mockClient.`when`(HttpRequest.request("/v1/user/auth-info").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }).respond(HttpResponse.response().apply {
                withBody(json(Json.encodeToString(mockResponse)))
            })

            val expected = UserInfoServiceRepoUserAuthInfoResponse.Invalid(listOf("test-error"))
            val actual = runBlocking { userInfoServiceRepo.getUserAuthInfo(request) }
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `NEGATIVE ~~ getUserAuthInfo ~~ request is not successful`() {
        MockServerClient(mockServer.host, mockServer.serverPort).use { mockClient ->
            val request = UserInfoServiceRepoGetUserAuthInfoRequest(
                "test-login",
                "test-password"
            )

            val expectations = mockClient.`when`(HttpRequest.request("/v1/user/auth-info").apply {
                withMethod("POST")
                withBody(json(Json.encodeToString(request)))
            }).respond(HttpResponse.response().apply {
                withStatusCode(500)
            })

            val actual = assertThrows<GetUserInfoException> {
                runBlocking { userInfoServiceRepo.getUserAuthInfo(request) }
            }
            assertEquals(request.login, actual.login)
            mockClient.verify(expectations.first().id, once())
        }
    }
}
