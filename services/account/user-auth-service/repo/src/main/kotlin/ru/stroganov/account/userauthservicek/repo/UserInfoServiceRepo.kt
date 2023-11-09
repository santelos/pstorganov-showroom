package ru.stroganov.account.userauthservicek.repo

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import ru.stroganov.account.userauthservice.common.BaseException.RepoException.*
import ru.stroganov.account.userauthservice.config.appConfig
import ru.stroganov.account.userauthservicek.repo.config.httpClient

@Serializable
data class CreateUserRepoRequest(
    val login: String,
    val password: String,
    val name: String
)

@Serializable
data class CreateUserRepoResponse(
    val userId: Int
)

@Serializable
data class GetUserIdRepoRequest(
    val login: String
)

@Serializable
data class GetUserIdRepoResponse(
    val userId: Int
)

@Serializable
data class GetUserAuthInfoRepoRequest(
    val userId: Int
)

@Serializable
data class GetUserAuthInfoRepoResponse(
    val passwordHash: String,
    val roles: Set<String>,
)

interface UserInfoServiceRepo {
    suspend fun getUserId(request: GetUserIdRepoRequest): GetUserIdRepoResponse
    suspend fun getUserAuthInfo(request: GetUserAuthInfoRepoRequest): GetUserAuthInfoRepoResponse
    suspend fun createUser(request: CreateUserRepoRequest): CreateUserRepoResponse
}

val userInfoServiceRepoImpl: UserInfoServiceRepo by lazy {
    UserInfoServiceRepoImpl(
        httpClient(appConfig.oauth2Client, appConfig.userInfoService.url)
    )
}
internal class UserInfoServiceRepoImpl(
    private val client: HttpClient
) : UserInfoServiceRepo {

    override suspend fun getUserId(request: GetUserIdRepoRequest): GetUserIdRepoResponse = runCatching {
        client.post("/v1/user/id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<GetUserIdRepoResponse>()
    }.getOrElse { throw HttpClientException(it) }

    override suspend fun getUserAuthInfo(request: GetUserAuthInfoRepoRequest): GetUserAuthInfoRepoResponse = runCatching {
        client.post("/v1/user/auth-info") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<GetUserAuthInfoRepoResponse>()
    }.getOrElse { throw HttpClientException(it) }

    override suspend fun createUser(request: CreateUserRepoRequest): CreateUserRepoResponse = runCatching {
        client.post("/v1/user") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<CreateUserRepoResponse>()
    }.getOrElse { throw HttpClientException(it) }
}
