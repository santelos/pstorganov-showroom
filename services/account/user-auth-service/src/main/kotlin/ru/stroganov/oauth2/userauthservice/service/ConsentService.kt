package ru.stroganov.oauth2.userauthservice.service

import org.springframework.stereotype.Service
import ru.stroganov.oauth2.userauthservice.repo.hydra.HydraAdminRepo
import ru.stroganov.oauth2.userauthservice.repo.hydra.response.GetConsentRepoResponse

interface ConsentService {
    suspend fun getConsent(consentChallenge: String): GetConsentRepoResponse
    suspend fun acceptConsent(consentChallenge: String, scope: List<String>): String
}

@Service
class ConsentServiceImpl(
    private val hydraAdminRepo: HydraAdminRepo,
) : ConsentService {

    override suspend fun getConsent(consentChallenge: String): GetConsentRepoResponse =
        hydraAdminRepo.getConsent(consentChallenge)

    override suspend fun acceptConsent(consentChallenge: String, scope: List<String>): String =
        hydraAdminRepo.acceptConsent(consentChallenge, scope).redirectTo
}