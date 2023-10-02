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
    fun getUserId() = runBlocking {
        val login = UserLogin("test--login")
        val dbResponse = UserIdRepoResponse.Success(UserId(1))
        coEvery { usersRepoMock.getUserId(any()) } returns dbResponse
        val expected = UserId(1)
        val actual = userService.getUserId(login)
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getUserId(login)
        }
        confirmVerified(usersRepoMock)
    }

    @Test
    fun `getUserAuthInfo ~~ Success`() = runBlocking {
        val input = UserId(1)
        val passwordHash = "test--password--hash"
        val roles = setOf("test:roles")
        val dbResponse = UserAuthInfoRepoResponse.Success(
            passwordHash = passwordHash,
            roles = roles
        )
        coEvery { usersRepoMock.getUserAuthInfo(any()) } returns dbResponse
        val expected = UserAuthInfo(
            passwordHash = passwordHash,
            roles = roles
        )
        val actual = userService.getUserAuthInfo(input)
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getUserAuthInfo(input)
        }
        confirmVerified(usersRepoMock)
    }

    @Test
    fun `getUserAuthInfo ~~ Exception`() = runBlocking {
        val input = UserId(1)
        val dbResponse = UserAuthInfoRepoResponse.UserNotFound
        coEvery { usersRepoMock.getUserAuthInfo(any()) } returns dbResponse
        val expected = ServiceException.UserIdNotFoundException(input)
        val actual = assertThrows<ServiceException.UserIdNotFoundException> {
            runBlocking { userService.getUserAuthInfo(input) }
        }
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getUserAuthInfo(input)
        }
        confirmVerified(usersRepoMock)
    }

    @Test
    fun `getUserPublicInfo ~~ Success`() = runBlocking {
        val input = UserId(1)
        val name = "test--name"
        val dbResponse = UserPublicInfoRepoResponse.Success(name)
        coEvery { usersRepoMock.getUserPublicInfo(any()) } returns dbResponse
        val expected = UserPublicInfo(name)
        val actual = userService.getUserPublicInfo(input)
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getUserPublicInfo(input)
        }
        confirmVerified(usersRepoMock)
    }

    @Test
    fun `getUserPublicInfo ~~ Exception`() = runBlocking {
        val input = UserId(1)
        val dbResponse = UserPublicInfoRepoResponse.UserNotFound
        coEvery { usersRepoMock.getUserPublicInfo(any()) } returns dbResponse
        val expected = ServiceException.UserIdNotFoundException(input)
        val actual = assertThrows<ServiceException.UserIdNotFoundException> {
            runBlocking { userService.getUserPublicInfo(input) }
        }
        assertEquals(expected, actual)
        coVerifyAll {
            usersRepoMock.getUserPublicInfo(input)
        }
        confirmVerified(usersRepoMock)
    }

    @Test
    fun `createUser ~~ Success`() = runBlocking {
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
}
