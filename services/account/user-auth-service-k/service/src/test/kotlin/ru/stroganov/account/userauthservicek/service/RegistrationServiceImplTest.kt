package ru.stroganov.account.userauthservicek.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.CreateUserException
import ru.stroganov.account.userauthservicek.common.UserId
import ru.stroganov.account.userauthservicek.repo.UserInfoServiceRepo
import ru.stroganov.account.userauthservicek.repo.UserInfoServiceRepoCreateUserRequest
import ru.stroganov.account.userauthservicek.repo.UserInfoServiceRepoCreateUserResponse
import kotlin.test.*

class RegistrationServiceImplTest {

    private val userServiceRepo: UserInfoServiceRepo = mockk()
    private val registrationService: RegistrationService = RegistrationServiceImpl(userServiceRepo)

    @Test
    fun `POSITIVE ~~ new`() {
        val input = RegistrationNewServiceRequest(
            "test-login",
            "test-password",
            "test-name"
        )

        val response = UserInfoServiceRepoCreateUserResponse(1)
        coEvery { userServiceRepo.createUser(any()) } returns response

        val expected = UserId(1)
        val actual = runBlocking { registrationService.new(input) }
        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            userServiceRepo.createUser(
                UserInfoServiceRepoCreateUserRequest(
                    "test-login",
                    "test-password",
                    "test-name"
                )
            )
        }
        confirmVerified(userServiceRepo)
    }

    @Test
    fun `NEGATIVE ~~ new ~~ repo throws exception`() {
        val input = RegistrationNewServiceRequest(
            "test-login",
            "test-password",
            "test-name"
        )

        val repoException = CreateUserException("test-login", RuntimeException())
        coEvery { userServiceRepo.createUser(any()) } throws repoException

        val actual = assertThrows<CreateUserException> {
            runBlocking { registrationService.new(input) }
        }
        assertEquals(repoException, actual)
        coVerify(exactly = 1) {
            userServiceRepo.createUser(
                UserInfoServiceRepoCreateUserRequest(
                    "test-login",
                    "test-password",
                    "test-name"
                )
            )
        }
        confirmVerified(userServiceRepo)
    }
}
