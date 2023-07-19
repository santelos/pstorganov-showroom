package ru.stroganov.account.userauthservice.service

import mu.KotlinLogging
import ru.stroganov.account.userauthservice.common.UserId
import ru.stroganov.account.userauthservice.repo.UserInfoServiceRepo
import ru.stroganov.account.userauthservice.repo.UserInfoServiceRepoGetUserAuthInfoRequest
import ru.stroganov.account.userauthservice.repo.UserInfoServiceRepoUserAuthInfoResponse.Invalid
import ru.stroganov.account.userauthservice.repo.UserInfoServiceRepoUserAuthInfoResponse.Success
import ru.stroganov.account.userauthservice.repo.userInfoServiceRepoImpl

sealed interface UserAuthInfo {
    data class AuthFailed(val errors: List<String>) : UserAuthInfo
    data class Success(val userId: UserId) : UserAuthInfo
}

interface RemoteAuthentication {
    suspend fun getUserAuthInfo(login: String, password: String): UserAuthInfo
}

val remoteAuthenticationImpl: RemoteAuthentication by lazy {
    RemoteAuthenticationImpl(userInfoServiceRepoImpl)
}
internal class RemoteAuthenticationImpl(
    private val userInfoServiceRepo: UserInfoServiceRepo
) : RemoteAuthentication {
    private val log = KotlinLogging.logger { }

    override suspend fun getUserAuthInfo(login: String, password: String): UserAuthInfo {
        val request = UserInfoServiceRepoGetUserAuthInfoRequest(login, password)
        return when (val result = userInfoServiceRepo.getUserAuthInfo(request)) {
            is Invalid -> {
                log.debug { "PasswordCheck. LOGIN: [$login]. Errors: [${result.errors}]" }
                UserAuthInfo.AuthFailed(result.errors)
            }
            is Success -> {
                log.debug { "PasswordCheck. LOGIN: [$login]. Valid" }
                UserAuthInfo.Success(UserId(result.userId))
            }
        }
    }
}
