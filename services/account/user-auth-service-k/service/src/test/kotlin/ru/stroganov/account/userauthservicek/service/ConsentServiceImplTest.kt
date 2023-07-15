package ru.stroganov.account.userauthservicek.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.AcceptConsentException
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.GetConsentException
import ru.stroganov.account.userauthservicek.repo.AcceptConsentRepoResponse
import ru.stroganov.account.userauthservicek.repo.GetConsentRepoResponse
import ru.stroganov.account.userauthservicek.repo.HydraAdminRepo
import kotlin.test.*

internal class ConsentServiceImplTest {

    private val hydraRepo: HydraAdminRepo = mockk()
    private val consentService = ConsentServiceImpl(hydraRepo)

    @Test
    fun `POSITIVE ~~ getConsent`() {
        val consentChallenge = "test-consent-challenge"

        val getConsentResponse = GetConsentRepoResponse(
            listOf("test-token-audience"),
            listOf("test-request-scope"),
            "test-subject"
        )
        coEvery { hydraRepo.getConsent(any()) } returns getConsentResponse

        val expected = GetConsentResponse(
            getConsentResponse.requestedAccessTokenAudience,
            getConsentResponse.requestedScope,
            getConsentResponse.subject
        )
        val actual = runBlocking { consentService.getConsent(consentChallenge) }
        assertEquals(expected, actual)
        coVerify(exactly = 1) { hydraRepo.getConsent(consentChallenge) }
        confirmVerified(hydraRepo)
    }

    @Test
    fun `NEGATIVE ~~ getConsent ~~ repo throws exception`() {
        val consentChallenge = "test-consent-challenge"

        val repoException = GetConsentException(consentChallenge, RuntimeException())
        coEvery { hydraRepo.getConsent(any()) } throws repoException

        val actual = assertThrows<GetConsentException> {
            runBlocking { consentService.getConsent(consentChallenge) }
        }
        assertEquals(repoException, actual)
        coVerify(exactly = 1) { hydraRepo.getConsent(consentChallenge) }
        confirmVerified(hydraRepo)
    }

    @Test
    fun `POSITIVE ~~ acceptConsent`() {
        val consentChallenge = "test-consent-challenge"
        val scope = listOf("test-scope")

        val acceptConsentResponse = AcceptConsentRepoResponse(
            "test-redirect"
        )
        coEvery { hydraRepo.acceptConsent(any(), any()) } returns acceptConsentResponse

        val expected = "test-redirect"
        val actual = runBlocking { consentService.acceptConsent(consentChallenge, scope) }
        assertEquals(expected, actual)
        coVerify(exactly = 1) { hydraRepo.acceptConsent(consentChallenge, scope) }
        confirmVerified(hydraRepo)
    }

    @Test
    fun `POSITIVE ~~ acceptConsent ~~ repo throws exception`() {
        val consentChallenge = "test-consent-challenge"
        val scope = listOf("test-scope")

        val repoException = AcceptConsentException(consentChallenge, scope, RuntimeException())
        coEvery { hydraRepo.acceptConsent(any(), any()) } throws repoException

        val actual = assertThrows<AcceptConsentException> {
            runBlocking { consentService.acceptConsent(consentChallenge, scope) }
        }
        assertEquals(repoException, actual)
        coVerify(exactly = 1) { hydraRepo.acceptConsent(consentChallenge, scope) }
        confirmVerified(hydraRepo)
    }
}
