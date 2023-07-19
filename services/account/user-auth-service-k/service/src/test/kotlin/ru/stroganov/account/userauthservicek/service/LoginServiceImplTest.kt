package ru.stroganov.account.userauthservicek.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.AcceptLoginException
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.GetLoginRequestException
import ru.stroganov.account.userauthservicek.common.UserId
import ru.stroganov.account.userauthservicek.repo.AcceptLoginRepoResponse
import ru.stroganov.account.userauthservicek.repo.HydraAdminRepo
import ru.stroganov.account.userauthservicek.repo.LoginRequestResponse
import kotlin.test.*

class LoginServiceImplTest {

    private val hydraRepo: HydraAdminRepo = mockk()
    private val loginService: LoginService = LoginServiceImpl(hydraRepo)

    @Test
    fun `POSITIVE ~~ acceptLogin`() {
        val userId = UserId(1)
        val loginRequest = "test-login-request"

        val loginRequestResponse = LoginRequestResponse("test-challenge")
        coEvery { hydraRepo.getLoginRequest(any()) } returns loginRequestResponse
        val acceptLoginRepoResponse = AcceptLoginRepoResponse("test-redirect-to")
        coEvery { hydraRepo.acceptLogin(any(), any()) } returns acceptLoginRepoResponse

        val expected = "test-redirect-to"
        val actual = runBlocking { loginService.acceptLogin(userId, loginRequest) }
        assertEquals(expected, actual)
        coVerify(exactly = 1) { hydraRepo.getLoginRequest(loginRequest) }
        coVerify(exactly = 1) { hydraRepo.acceptLogin("test-challenge", "1") }
        confirmVerified(hydraRepo)
    }

    @Test
    fun `NEGATIVE ~~ acceptLogin ~~ getLoginRequest throws exception`() {
        val userId = UserId(1)
        val loginRequest = "test-login-request"

        val repoException = GetLoginRequestException(loginRequest, RuntimeException())
        coEvery { hydraRepo.getLoginRequest(any()) } throws repoException

        val actual = assertThrows<GetLoginRequestException> {
            runBlocking { loginService.acceptLogin(userId, loginRequest) }
        }
        assertEquals(repoException, actual)
        coVerify(exactly = 1) { hydraRepo.getLoginRequest(loginRequest) }
        confirmVerified(hydraRepo)
    }

    @Test
    fun `NEGATIVE ~~ acceptLogin ~~ acceptLogin throws exception`() {
        val userId = UserId(1)
        val loginRequest = "test-login-request"

        val loginRequestResponse = LoginRequestResponse("test-challenge")
        coEvery { hydraRepo.getLoginRequest(any()) } returns loginRequestResponse
        val repoException = AcceptLoginException("test-redirect-to", "1", RuntimeException())
        coEvery { hydraRepo.acceptLogin(any(), any()) } throws repoException

        val actual = assertThrows<AcceptLoginException> {
            runBlocking { loginService.acceptLogin(userId, loginRequest) }
        }
        assertEquals(repoException, actual)
        coVerify(exactly = 1) { hydraRepo.getLoginRequest(loginRequest) }
        coVerify(exactly = 1) { hydraRepo.acceptLogin("test-challenge", "1") }
        confirmVerified(hydraRepo)
    }
}
