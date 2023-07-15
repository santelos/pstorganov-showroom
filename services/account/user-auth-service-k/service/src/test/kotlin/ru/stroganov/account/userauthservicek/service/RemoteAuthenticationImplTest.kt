package ru.stroganov.account.userauthservicek.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.GetUserInfoException
import ru.stroganov.account.userauthservicek.common.UserId
import ru.stroganov.account.userauthservicek.repo.UserInfoServiceRepo
import ru.stroganov.account.userauthservicek.repo.UserInfoServiceRepoGetUserAuthInfoRequest
import ru.stroganov.account.userauthservicek.repo.UserInfoServiceRepoUserAuthInfoResponse
import kotlin.test.*

class RemoteAuthenticationImplTest {

    private val userInfoServiceRepo: UserInfoServiceRepo = mockk()
    private val remoteAuthentication: RemoteAuthentication = RemoteAuthenticationImpl(
        userInfoServiceRepo
    )

    @Test
    fun `POSITIVE ~~ getUserAuthInfo ~~ success`() {
        val login = "test-login"
        val password = "test-password"

        val repoResponse = UserInfoServiceRepoUserAuthInfoResponse.Success(1)
        coEvery { userInfoServiceRepo.getUserAuthInfo(any()) } returns repoResponse

        val expected = UserAuthInfo.Success(UserId(1))
        val actual = runBlocking { remoteAuthentication.getUserAuthInfo(login, password) }
        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            userInfoServiceRepo.getUserAuthInfo(UserInfoServiceRepoGetUserAuthInfoRequest(login, password))
        }
        confirmVerified(userInfoServiceRepo)
    }

    @Test
    fun `POSITIVE ~~ getUserAuthInfo ~~ no user`() {
        val login = "test-login"
        val password = "test-password"

        val repoResponse = UserInfoServiceRepoUserAuthInfoResponse.Invalid(listOf("test-error"))
        coEvery { userInfoServiceRepo.getUserAuthInfo(any()) } returns repoResponse

        val expected = UserAuthInfo.AuthFailed(listOf("test-error"))
        val actual = runBlocking { remoteAuthentication.getUserAuthInfo(login, password) }
        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            userInfoServiceRepo.getUserAuthInfo(UserInfoServiceRepoGetUserAuthInfoRequest(login, password))
        }
        confirmVerified(userInfoServiceRepo)
    }

    @Test
    fun `NEGATIVE ~~ getUserAuthInfo ~~ repo throws exception`() {
        val login = "test-login"
        val password = "test-password"

        val repoResponse = GetUserInfoException(login, RuntimeException())
        coEvery { userInfoServiceRepo.getUserAuthInfo(any()) } throws repoResponse

        val actual = assertThrows<GetUserInfoException> {
            runBlocking { remoteAuthentication.getUserAuthInfo(login, password) }
        }
        assertEquals(repoResponse, actual)
        coVerify(exactly = 1) {
            userInfoServiceRepo.getUserAuthInfo(UserInfoServiceRepoGetUserAuthInfoRequest(login, password))
        }
        confirmVerified(userInfoServiceRepo)
    }
}
