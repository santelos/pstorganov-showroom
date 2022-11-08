package ru.stroganov.oauth2.userauthservice.repo

import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import ru.stroganov.oauth2.userauthservice.repo.request.CheckUserPasswordRequest
import ru.stroganov.oauth2.userauthservice.repo.request.GetUserAuthInfoRequest
import ru.stroganov.oauth2.userauthservice.repo.response.CheckUserPasswordResponse
import ru.stroganov.oauth2.userauthservice.repo.response.UserAuthInfoResponse

interface UserServiceRepo {
    suspend fun getUserAuthInfo(input: GetUserAuthInfoRequest): UserAuthInfoResponse
    suspend fun checkUserPassword(input: CheckUserPasswordRequest): CheckUserPasswordResponse
}

@Repository
class UserServiceInMemoryRepoImpl(
    private val webClient: WebClient
) : UserServiceRepo {

    private data class User(
        val username: String,
        val password: String,
    )
    private val users: List<User> = listOf(
        User("user", "password")
    )

    override suspend fun getUserAuthInfo(input: GetUserAuthInfoRequest): UserAuthInfoResponse {
        val user = users.single { it.username == input.username }
        return UserAuthInfoResponse(user.username)
    }

    override suspend fun checkUserPassword(input: CheckUserPasswordRequest): CheckUserPasswordResponse {
        val user = users.single { it.username == input.username }
        val isCorrect = user.password == input.passwordHash
        return CheckUserPasswordResponse(isCorrect)
    }
}