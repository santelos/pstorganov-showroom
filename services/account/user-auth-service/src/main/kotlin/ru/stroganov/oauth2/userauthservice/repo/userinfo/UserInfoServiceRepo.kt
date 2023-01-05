package ru.stroganov.oauth2.userauthservice.repo.userinfo

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

data class UserInfoServiceRepoCreateUserRequest(
    val login: String,
    val password: String,
    val name: String,
)

data class UserInfoServiceRepoCreateUserResponse(
    val userId: Int,
)

data class UserInfoServiceRepoGetUserAuthInfoRequest(
    val login: String,
    val password: String,
)

data class UserInfoServiceRepoUserAuthInfoResponseInternal(
    val isValid: Boolean,
    val errors: List<String>,
    val userId: Int,
)
sealed interface UserInfoServiceRepoUserAuthInfoResponse {
    data class Success(val userId: Int) : UserInfoServiceRepoUserAuthInfoResponse
    data class Invalid(val errors: List<String>) : UserInfoServiceRepoUserAuthInfoResponse
}

interface UserInfoServiceRepo {
    suspend fun createUser(
        request: UserInfoServiceRepoCreateUserRequest
    ) : UserInfoServiceRepoCreateUserResponse
    suspend fun getUserAuthInfo(
        request: UserInfoServiceRepoGetUserAuthInfoRequest
    ) : UserInfoServiceRepoUserAuthInfoResponse
}

@Repository
internal class UserInfoServiceRepoImpl(
    private val webClient: WebClient,
    @Value("\${user-info-service.url}") private val host: String,
) : UserInfoServiceRepo {

    override suspend fun createUser(
        request: UserInfoServiceRepoCreateUserRequest
    ) : UserInfoServiceRepoCreateUserResponse = webClient.post()
        .uri("$host/v1/user")
        .body(BodyInserters.fromValue(request))
        .awaitExchange {
            it.awaitBody()
        }

    override suspend fun getUserAuthInfo(
        request: UserInfoServiceRepoGetUserAuthInfoRequest
    ): UserInfoServiceRepoUserAuthInfoResponse = webClient.post()
        .uri("$host/v1/user/auth-info")
        .body(BodyInserters.fromValue(request))
        .awaitExchange {
            it.awaitBody<UserInfoServiceRepoUserAuthInfoResponseInternal>()
        }.let {
            if (it.isValid) UserInfoServiceRepoUserAuthInfoResponse.Success(it.userId)
            else UserInfoServiceRepoUserAuthInfoResponse.Invalid(it.errors)
        }
}
