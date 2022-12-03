package ru.stroganov.oauth2.userauthservice.service

import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono
import ru.stroganov.oauth2.userauthservice.exception.authentication.UserAuthFailedException
import ru.stroganov.oauth2.userauthservice.repo.UserServiceRepo
import ru.stroganov.oauth2.userauthservice.repo.request.CheckUserPasswordRequest
import ru.stroganov.oauth2.userauthservice.repo.request.GetUserAuthInfoRequest
import ru.stroganov.oauth2.userauthservice.repo.response.UserAuthInfoResponse
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepo
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepoVerifyCredentialsRequest
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepoVerifyCredentialsResponse

class RemoteReactiveAuthenticationManager(
    private val userInfoServiceRepo: UserInfoServiceRepo,
) : ReactiveAuthenticationManager {

    private val log = KotlinLogging.logger {  }

    override fun authenticate(authentication: Authentication): Mono<Authentication> = mono {
        val login = authentication.name
        val password = authentication.credentials as String
        val checks = authChecks(login, password)
        log.debug { "Authenticate. Checks result: [$checks]" }
        return@mono if (checks.isNotEmpty()) {
            throw UserAuthFailedException(checks.toList())
        } else {
            UsernamePasswordAuthenticationToken.authenticated(login, password, listOf(
                SimpleGrantedAuthority("ROLE_USER")
            ))
        }
    }

    private suspend fun authChecks(login: String, password: String): List<String> = buildList {
        passwordCheck(login, password)
    }

    private suspend fun MutableList<String>.passwordCheck(login: String, password: String) {
        val request = UserInfoServiceRepoVerifyCredentialsRequest(login, password)
        val result = userInfoServiceRepo.verifyCredentials(request)
        if (result is UserInfoServiceRepoVerifyCredentialsResponse.Invalid) {
            log.debug { "PasswordCheck. LOGIN: [$login]. Errors: [${result.errors}]" }
            addAll(result.errors)
        } else {
            log.debug { "PasswordCheck. LOGIN: [$login]. Valid" }
        }
    }
}
