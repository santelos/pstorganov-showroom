package ru.stroganov.oauth2.userauthservice.service

import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import ru.stroganov.oauth2.userauthservice.repo.hydra.HydraAdminRepo

interface LoginAcceptService {
    suspend fun acceptLogin(auth: Authentication, loginRequest: String): String
}

@Service
class LoginAcceptServiceImpl(
    private val hydraAdminRepo: HydraAdminRepo,
) : LoginAcceptService {
    private val log = KotlinLogging.logger {  }

    override suspend fun acceptLogin(auth: Authentication, loginRequest: String): String {
        log.info { loginRequest }
        val loginRequestResp = hydraAdminRepo.getLoginRequest(loginRequest)
        return hydraAdminRepo.acceptLogin(loginRequestResp.challenge, auth.name)
    }
}