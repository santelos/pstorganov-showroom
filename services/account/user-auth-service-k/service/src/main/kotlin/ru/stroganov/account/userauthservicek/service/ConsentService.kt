package ru.stroganov.account.userauthservicek.service

import mu.KotlinLogging
import ru.stroganov.account.userauthservicek.repo.GetConsentRepoResponse
import ru.stroganov.account.userauthservicek.repo.HydraAdminRepo
import ru.stroganov.account.userauthservicek.repo.hydraAdminRepoImpl

interface ConsentService {
    suspend fun getConsent(consentChallenge: String): GetConsentRepoResponse
    suspend fun acceptConsent(consentChallenge: String, scope: List<String>): String
}

val consentServiceImpl: ConsentService by lazy {
    ConsentServiceImpl(hydraAdminRepoImpl)
}
internal class ConsentServiceImpl(
    private val hydraAdminRepo: HydraAdminRepo,
) : ConsentService {
    private val log = KotlinLogging.logger {  }

    override suspend fun getConsent(consentChallenge: String): GetConsentRepoResponse {
        log.info { "Getting a consent details. Challenge: [$consentChallenge]" }
        return hydraAdminRepo.getConsent(consentChallenge)
    }

    override suspend fun acceptConsent(consentChallenge: String, scope: List<String>): String {
        log.info { "Accepting consent. Challenge: [$consentChallenge]" }
        return hydraAdminRepo.acceptConsent(consentChallenge, scope).redirectTo
    }
}
