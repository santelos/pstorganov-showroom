package ru.stroganov.oauth2.userauthservice.service

import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono
import ru.stroganov.oauth2.userauthservice.exception.authentication.UserAuthFailedException
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepo
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepoGetUserAuthInfoRequest
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepoUserAuthInfoResponse
import ru.stroganov.oauth2.userauthservice.service.model.UserId

private sealed interface UserAuthInfo {
    data class AuthFailed(val errors: List<String>) : UserAuthInfo
    data class Success(val userId: UserId) : UserAuthInfo
}

class RemoteReactiveAuthenticationManager(
    private val userInfoServiceRepo: UserInfoServiceRepo,
) : ReactiveAuthenticationManager {
    private val log = KotlinLogging.logger {  }

    override fun authenticate(authentication: Authentication): Mono<Authentication> = mono {
        val login = authentication.name
        val password = authentication.credentials as String
        when (val authInfo = getUserAuthInfo(login, password)) {
            is UserAuthInfo.AuthFailed -> throw UserAuthFailedException(authInfo.errors)
            is UserAuthInfo.Success -> UsernamePasswordAuthenticationToken.authenticated(
                authInfo.userId.id, password,
                listOf(SimpleGrantedAuthority("ROLE_USER"))
            )
        }
    }

    private suspend fun getUserAuthInfo(login: String, password: String): UserAuthInfo {
        val request = UserInfoServiceRepoGetUserAuthInfoRequest(login, password)
        return when (val result = userInfoServiceRepo.getUserAuthInfo(request)) {
            is UserInfoServiceRepoUserAuthInfoResponse.Invalid -> {
                log.debug { "PasswordCheck. LOGIN: [$login]. Errors: [${result.errors}]" }
                UserAuthInfo.AuthFailed(result.errors)
            }
            is UserInfoServiceRepoUserAuthInfoResponse.Success -> {
                log.debug { "PasswordCheck. LOGIN: [$login]. Valid" }
                UserAuthInfo.Success(UserId(result.userId))
            }
        }
    }
}
