package ru.stroganov.account.userauthservicek.service

import mu.KotlinLogging
import ru.stroganov.account.userauthservicek.repo.HydraAdminRepo
import ru.stroganov.account.userauthservicek.repo.hydraAdminRepoImpl

data class GetConsentResponse(
    val requestedAccessTokenAudience: List<String>?,
    val requestedScope: List<String>?,
    val subject: String?
)

interface ConsentService {
    suspend fun getConsent(consentChallenge: String): GetConsentResponse
    suspend fun acceptConsent(consentChallenge: String, scope: List<String>): String
}

val consentServiceImpl: ConsentService by lazy {
    ConsentServiceImpl(hydraAdminRepoImpl)
}
internal class ConsentServiceImpl(
    private val hydraAdminRepo: HydraAdminRepo
) : ConsentService {
    private val log = KotlinLogging.logger { }

    override suspend fun getConsent(consentChallenge: String): GetConsentResponse {
        log.info { "Getting a consent details. Challenge: [$consentChallenge]" }
        val repoResp = hydraAdminRepo.getConsent(consentChallenge)
        return GetConsentResponse(
            repoResp.requestedAccessTokenAudience,
            repoResp.requestedScope,
            repoResp.subject
        )
    }

    override suspend fun acceptConsent(consentChallenge: String, scope: List<String>): String {
        log.info { "Accepting consent. Challenge: [$consentChallenge]" }
        val repoResp = hydraAdminRepo.acceptConsent(consentChallenge, scope)
        return repoResp.redirectTo
    }
}
