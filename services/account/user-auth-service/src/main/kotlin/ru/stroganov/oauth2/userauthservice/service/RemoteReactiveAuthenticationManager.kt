package ru.stroganov.oauth2.userauthservice.service

import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import ru.stroganov.oauth2.userauthservice.exception.authentication.UserAuthFailedException
import ru.stroganov.oauth2.userauthservice.repo.UserServiceRepo
import ru.stroganov.oauth2.userauthservice.repo.request.CheckUserPasswordRequest
import ru.stroganov.oauth2.userauthservice.repo.request.GetUserAuthInfoRequest
import ru.stroganov.oauth2.userauthservice.repo.response.CheckUserPasswordResponse
import ru.stroganov.oauth2.userauthservice.repo.response.UserAuthInfoResponse

@Service
class RemoteReactiveAuthenticationManager(
    private val userServiceRepo: UserServiceRepo,
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> = mono {
        val username = authentication.name
        val passwordHash = authentication.credentials as String
        val userAuthInfo = userServiceRepo.getUserAuthInfo(GetUserAuthInfoRequest(username))
        val checks = authChecks(userAuthInfo, passwordHash)
        return@mono if (checks.isNotEmpty()) {
            throw UserAuthFailedException(checks.toList())
        } else {
            UsernamePasswordAuthenticationToken.authenticated(userAuthInfo.username, passwordHash, listOf(
                SimpleGrantedAuthority("ROLE_USER")
            ))
        }
    }

    private suspend fun authChecks(userAuthInfo: UserAuthInfoResponse, passwordHash: String): List<String> {
        val checks = mutableListOf<String>()
        if (!passwordCheck(userAuthInfo, passwordHash)) checks.add("Password")
        return checks
    }

    private suspend fun passwordCheck(userAuthInfo: UserAuthInfoResponse, passwordHash: String): Boolean {
        val request = CheckUserPasswordRequest(userAuthInfo.username, passwordHash)
        val response = userServiceRepo.checkUserPassword(request)
        return response.isCorrect
    }
}
