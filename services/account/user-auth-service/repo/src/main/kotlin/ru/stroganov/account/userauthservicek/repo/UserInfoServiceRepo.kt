package ru.stroganov.account.userauthservice.repo

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import ru.stroganov.account.userauthservice.common.BaseException.RepoException.CreateUserException
import ru.stroganov.account.userauthservice.common.BaseException.RepoException.GetUserInfoException
import ru.stroganov.account.userauthservice.config.AppConfig
import ru.stroganov.account.userauthservice.config.appConfig
import ru.stroganov.account.userauthservice.repo.config.httpClient

@Serializable
data class UserInfoServiceRepoCreateUserRequest(
    val login: String,
    val password: String,
    val name: String
)

@Serializable
data class UserInfoServiceRepoCreateUserResponse(
    val userId: Int
)

@Serializable
data class UserInfoServiceRepoGetUserAuthInfoRequest(
    val login: String,
    val password: String
)

@Serializable
data class UserInfoServiceRepoUserAuthInfoResponseInternal(
    val isValid: Boolean,
    val errors: List<String>,
    val userId: Int
)
sealed interface UserInfoServiceRepoUserAuthInfoResponse {
    data class Success(val userId: Int) : UserInfoServiceRepoUserAuthInfoResponse
    data class Invalid(val errors: List<String>) : UserInfoServiceRepoUserAuthInfoResponse
}

interface UserInfoServiceRepo {
    suspend fun createUser(
        request: UserInfoServiceRepoCreateUserRequest
    ): UserInfoServiceRepoCreateUserResponse
    suspend fun getUserAuthInfo(
        request: UserInfoServiceRepoGetUserAuthInfoRequest
    ): UserInfoServiceRepoUserAuthInfoResponse
}

val userInfoServiceRepoImpl: UserInfoServiceRepo by lazy {
    UserInfoServiceRepoImpl(
        httpClient,
        appConfig.userInfoService
    )
}
internal class UserInfoServiceRepoImpl(
    private val client: HttpClient,
    private val config: AppConfig.UserInfoService
) : UserInfoServiceRepo {

    override suspend fun createUser(
        request: UserInfoServiceRepoCreateUserRequest
    ): UserInfoServiceRepoCreateUserResponse = runCatching {
        client
            .post("${config.host}/v1/user") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<UserInfoServiceRepoCreateUserResponse>()
    }.getOrElse { throw CreateUserException(request.login, it) }

    override suspend fun getUserAuthInfo(
        request: UserInfoServiceRepoGetUserAuthInfoRequest
    ): UserInfoServiceRepoUserAuthInfoResponse = runCatching {
        client
            .post("${config.host}/v1/user/auth-info") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body<UserInfoServiceRepoUserAuthInfoResponseInternal>()
            .let {
                if (it.isValid) {
                    UserInfoServiceRepoUserAuthInfoResponse.Success(it.userId)
                } else {
                    UserInfoServiceRepoUserAuthInfoResponse.Invalid(it.errors)
                }
            }
    }.getOrElse { throw GetUserInfoException(request.login, it) }
}
