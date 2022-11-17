package ru.stroganov.showroom.account.userinfoservice.service

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.stroganov.showroom.account.userinfoservice.repo.CreateUserRepoRequest
import ru.stroganov.showroom.account.userinfoservice.repo.UserCredentialsRepoResponse
import ru.stroganov.showroom.account.userinfoservice.repo.UserInfoRepoResponse
import ru.stroganov.showroom.account.userinfoservice.repo.UsersRepo

internal class UserServiceImplTest {

    private val usersRepoMock: UsersRepo = mockk()
    private val hashingMock: Hashing = mockk()
    private val userService: UserService = UserServiceImpl(usersRepoMock, hashingMock)

    @Test
    fun createUser() = runBlocking {
        val input = NewUser(
            login = "test--login",
            password = "test--password",
            name = "test--name",
        )
        val passwordHash = "test--password-hashed"
        every { hashingMock.hash(input.password) } returns passwordHash
        val createUserRepoRequest = CreateUserRepoRequest(
            login = input.login,
            passwordHash = passwordHash,
            name = input.name,
        )
        val expected = UserId(1)
        coEvery { usersRepoMock.createUser(createUserRepoRequest) } returns expected
        val actual = userService.createUser(input)
        assertEquals(expected, actual)
    }

    @Test
    fun getUserInfo() = runBlocking {
        val input = UserId(1)
        val expected = UserInfo(
            id = input.id,
            name = "test--name",
        )
        val dbResponse = UserInfoRepoResponse(
            id = expected.id,
            name = expected.name,
        )
        coEvery { usersRepoMock.getUserInfo(input) } returns dbResponse
        val actual = userService.getUserInfo(input)
        assertEquals(expected, actual)
    }

    @Test
    fun getUserCredentials() = runBlocking {
        val input = UserId(1)
        val expected = UserCredentials(
            login = "test--login",
            passwordHash = "test--password-hash",
        )
        val dbResponse = UserCredentialsRepoResponse(
            login = expected.login,
            passwordHash = expected.passwordHash,
        )
        coEvery { usersRepoMock.getUserCredentials(input) } returns dbResponse
        val actual = userService.getUserCredentials(input)
        assertEquals(expected, actual)
    }
}
