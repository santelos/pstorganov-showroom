package ru.stroganov.account.userauthservicek.service

import mu.KotlinLogging
import ru.stroganov.account.userauthservicek.common.UserId
import ru.stroganov.account.userauthservicek.repo.HydraAdminRepo
import ru.stroganov.account.userauthservicek.repo.hydraAdminRepoImpl

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
