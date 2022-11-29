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

data class UserInfoServiceRepoVerifyCredentialsRequest(
    val login: String,
    val passwordHash: String,
)

data class UserInfoServiceRepoVerifyCredentialsResponseInternal(
    val isValid: Boolean,
    val errors: List<String>,
)
sealed interface UserInfoServiceRepoVerifyCredentialsResponse{
    object Valid : UserInfoServiceRepoVerifyCredentialsResponse
    data class Invalid(val errors: List<String>) : UserInfoServiceRepoVerifyCredentialsResponse
}

interface UserInfoServiceRepo {
    suspend fun createUser(
        request: UserInfoServiceRepoCreateUserRequest
    ) : UserInfoServiceRepoCreateUserResponse
    suspend fun verifyCredentials(
        request: UserInfoServiceRepoVerifyCredentialsRequest
    ) : UserInfoServiceRepoVerifyCredentialsResponse
}

@Repository
internal class UserInfoServiceRepoImpl(
    private val webClient: WebClient,
    @Value("service.user-info-service.host") private val host: String,
) : UserInfoServiceRepo {

    override suspend fun createUser(
        request: UserInfoServiceRepoCreateUserRequest
    ) : UserInfoServiceRepoCreateUserResponse = webClient.post()
        .uri("$host/v1/user")
        .body(BodyInserters.fromValue(request))
        .awaitExchange {
            it.awaitBody()
        }

    override suspend fun verifyCredentials(
        request: UserInfoServiceRepoVerifyCredentialsRequest
    ): UserInfoServiceRepoVerifyCredentialsResponse = webClient.post()
        .uri("$host/v1/user/credentials")
        .body(BodyInserters.fromValue(request))
        .awaitExchange {
            it.awaitBody<UserInfoServiceRepoVerifyCredentialsResponseInternal>()
        }.let {
            if (it.isValid) UserInfoServiceRepoVerifyCredentialsResponse.Valid
            else UserInfoServiceRepoVerifyCredentialsResponse.Invalid(it.errors)
        }
}
