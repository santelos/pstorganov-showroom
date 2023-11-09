package ru.stroganov.account.userauthservicek.service

import mu.KotlinLogging
import ru.stroganov.account.userauthservicek.repo.*

data class GetConsentResponse(
    val requestedAccessTokenAudience: List<String>,
    val requestedScope: List<String>,
    val subject: String
)

interface ConsentService {
    suspend fun acceptConsent(consentChallenge: String, scope: List<String>): String
}

val consentServiceImpl: ConsentService by lazy {
    ConsentServiceImpl(hydraAdminRepoImpl, userInfoServiceRepoImpl)
}
internal class ConsentServiceImpl(
    private val hydraAdminRepo: HydraAdminRepo,
    private val userInfoServiceRepo: UserInfoServiceRepo,
) : ConsentService {
    private val log = KotlinLogging.logger { }

    override suspend fun getConsent(consentChallenge: String): GetConsentResponse {
        return GetConsentResponse(
            repoResp.requestedAccessTokenAudience,
            repoResp.requestedScope,
            repoResp.subject
        )
    }

    override suspend fun acceptConsent(consentChallenge: String, scope: List<String>): String {
        log.info { "Accepting consent. Challenge: [$consentChallenge]" }
        val consent = hydraAdminRepo.getConsent(consentChallenge)
        val userAuthInfoRequest = GetUserAuthInfoRepoRequest(consent.subject.toInt())
        val userAuthInfo = userInfoServiceRepo.getUserAuthInfo(userAuthInfoRequest)
        val intersectedScope = userAuthInfo.roles.intersect(consent.requestedScope.toSet())
        val repoResp = hydraAdminRepo.acceptConsent(consentChallenge, intersectedScope)
        return repoResp.redirectTo
    }
}
