package ru.stroganov.account.userauthservice.service

import mu.KotlinLogging
import ru.stroganov.account.userauthservice.common.UserId
import ru.stroganov.account.userauthservice.repo.HydraAdminRepo
import ru.stroganov.account.userauthservice.repo.hydraAdminRepoImpl

interface LoginService {
    suspend fun acceptLogin(user: UserId, loginRequest: String): String
}

val loginServiceImpl: LoginService by lazy {
    LoginServiceImpl(hydraAdminRepoImpl)
}
internal class LoginServiceImpl(
    private val hydraAdminRepo: HydraAdminRepo
) : LoginService {
    private val log = KotlinLogging.logger { }

    override suspend fun acceptLogin(user: UserId, loginRequest: String): String {
        log.info { "Accepting login request. Request: [$loginRequest]" }
        val loginRequestResp = hydraAdminRepo.getLoginRequest(loginRequest)
        return hydraAdminRepo.acceptLogin(loginRequestResp.challenge, user.id.toString()).redirectTo
    }
}
