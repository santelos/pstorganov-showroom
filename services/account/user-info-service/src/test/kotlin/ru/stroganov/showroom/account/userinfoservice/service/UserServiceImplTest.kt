package ru.stroganov.showroom.account.userinfoservice.service

import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import ru.stroganov.showroom.account.userinfoservice.common.ServiceException
import ru.stroganov.showroom.account.userinfoservice.repo.*
import kotlin.test.*

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
            roles = setOf("test:role")
        )
        val passwordHash = "test--password-hashed"
        every { hashingMock.hash(any()) } returns passwordHash
        val createUserRepoRequest = CreateUserRepoRequest(
            login = input.login,
            passwordHash = passwordHash,
            name = input.name,
            roles = setOf("test:role")
        )
        val expected = UserId(1)
        coEvery { usersRepoMock.createUser(any()) } returns expected
        val actual = userService.createUser(input)
        assertEquals(expected, actual)
        coVerifyAll {
            hashingMock.hash(input.password)
            usersRepoMock.createUser(createUserRepoRequest)
        }
        confirmVerified(hashingMock, usersRepoMock)
    }

    @Test
    fun getUserInfo() = runBlocking {
        val input = UserId(1)
        val expected = UserInfo(
            id = input.id,
            name = "test--name",
        )
        val dbResponse = UserInfoRepoResponse.Success(
            id = expected.id,
            name = expected.name,
            roles = setOf("test:roles")
        )
        coEvery { usersRepoMock.getUserInfo(any()) } returns dbResponse
        val actual = userService.getUserInfo(input)
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getUserInfo(input)
        }
        confirmVerified(usersRepoMock)
    }

    @Test
    fun `getUserAuthInfo ~~ Valid`() = runBlocking {
        val input = UserCredentials(
            login = UserLogin("test--login"),
            password = "test--password",
        )

        val passwordHash = "test--password--hash"
        val passwordHashResponse = GetPasswordHashResponse.Success(passwordHash)
        coEvery { usersRepoMock.getPasswordHash(any()) } returns passwordHashResponse
        every { hashingMock.isEqual(input.password, passwordHash) } returns true
        val userIdResponse = UserIdRepoResponse.Success(1)
        coEvery { usersRepoMock.getUserId(any()) } returns userIdResponse

        val expected = UserAuthInfo.Success(UserId(1))
        val actual = userService.getUserAuthInfo(input)
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getPasswordHash(input.login)
            usersRepoMock.getUserId(input.login)
            hashingMock.isEqual(input.password, passwordHash)
        }
        confirmVerified(usersRepoMock, hashingMock)
    }

    @Test
    fun `validateCredentials ~~ PasswordHash ~~ User not found`() = runBlocking {
        val input = UserCredentials(
            login = UserLogin("test--login"),
            password = "test--password",
        )

        val dbResponse = GetPasswordHashResponse.UserNotFound
        coEvery { usersRepoMock.getPasswordHash(any()) } returns dbResponse

        val expected = UserAuthInfo.Invalid(listOf("User not found"))
        val actual = userService.getUserAuthInfo(input)
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getPasswordHash(input.login)
        }
        confirmVerified(usersRepoMock)
    }

    @Test
    fun `validateCredentials ~~ Password doesn't `() = runBlocking {
        val input = UserCredentials(
            login = UserLogin("test--login"),
            password = "test--password",
        )

        val passwordHash = "test--password--hash"
        val dbResponse = GetPasswordHashResponse.Success(passwordHash)
        coEvery { usersRepoMock.getPasswordHash(any()) } returns dbResponse
        every { hashingMock.isEqual(any(), any()) } returns false

        val expected = UserAuthInfo.Invalid(listOf("Password doesn't match"))
        val actual = userService.getUserAuthInfo(input)
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getPasswordHash(input.login)
            hashingMock.isEqual(input.password, passwordHash)
        }
        confirmVerified(usersRepoMock, hashingMock)
    }

    @Test
    fun `validateCredentials ~~ UserId ~~ User Not Found`() = runBlocking {
        val input = UserCredentials(
            login = UserLogin("test--login"),
            password = "test--password",
        )

        val passwordHash = "test--password--hash"
        val dbResponse = GetPasswordHashResponse.Success(passwordHash)
        coEvery { usersRepoMock.getPasswordHash(any()) } returns dbResponse
        every { hashingMock.isEqual(any(), any()) } returns true
        val userIdResponse = UserIdRepoResponse.UserNotFound
        coEvery { usersRepoMock.getUserId(any()) } returns userIdResponse

        val expected = ServiceException.UserLoginNotFound(input.login)
        val actual = assertThrows<ServiceException.UserLoginNotFound> {
            runBlocking { userService.getUserAuthInfo(input) }
        }
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getPasswordHash(input.login)
            hashingMock.isEqual(input.password, passwordHash)
            usersRepoMock.getUserId(input.login)
        }
        confirmVerified(usersRepoMock, hashingMock)
    }
}
