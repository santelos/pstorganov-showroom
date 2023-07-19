package ru.stroganov.showroom.account.userinfoservice.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import ru.stroganov.showroom.account.userinfoservice.common.ServiceException
import ru.stroganov.showroom.account.userinfoservice.repo.*

internal class UserServiceImplTest : FunSpec({
    val usersRepoMock: UsersRepo = mockk()
    val hashingMock: Hashing = mockk()
    val userService: UserService = UserServiceImpl(usersRepoMock, hashingMock)

    test("createUser") {
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
        expected shouldBe actual
    }

    test("getUserInfo") {
        val input = UserId(1)
        val expected = UserInfo(
            id = input.id,
            name = "test--name",
        )
        val dbResponse = UserInfoRepoResponse.Success(
            id = expected.id,
            name = expected.name,
        )
        coEvery { usersRepoMock.getUserInfo(input) } returns dbResponse
        val actual = userService.getUserInfo(input)
        expected shouldBe actual
    }

    test("getUserAuthInfo | Valid") {
        val input = UserCredentials(
            login = UserLogin("test--login"),
            password = "test--password",
        )

        val passwordHash = "test--password--hash"
        val passwordHashResponse = GetPasswordHashResponse.Success(passwordHash)
        coEvery { usersRepoMock.getPasswordHash(input.login) } returns passwordHashResponse
        every { hashingMock.isEqual(input.password, passwordHash) } returns true
        val userIdResponse = UserIdRepoResponse.Success(1)
        coEvery { usersRepoMock.getUserId(input.login) } returns userIdResponse

        val expected = UserAuthInfo.Success(UserId(1))
        val actual = userService.getUserAuthInfo(input)
        actual shouldBe expected
    }

    test("validateCredentials | [PasswordHash] User not found") {
        val input = UserCredentials(
            login = UserLogin("test--login"),
            password = "test--password",
        )

        val dbResponse = GetPasswordHashResponse.UserNotFound
        coEvery { usersRepoMock.getPasswordHash(input.login) } returns dbResponse

        val expected = UserAuthInfo.Invalid(listOf("User not found"))
        val actual = userService.getUserAuthInfo(input)
        actual shouldBe expected
    }

    test("validateCredentials | Password doesn't match") {
        val input = UserCredentials(
            login = UserLogin("test--login"),
            password = "test--password",
        )

        val passwordHash = "test--password--hash"
        val dbResponse = GetPasswordHashResponse.Success(passwordHash)
        coEvery { usersRepoMock.getPasswordHash(input.login) } returns dbResponse
        every { hashingMock.isEqual(input.password, passwordHash) } returns false

        val expected = UserAuthInfo.Invalid(listOf("Password doesn't match"))
        val actual = userService.getUserAuthInfo(input)
        actual shouldBe expected
    }

    test("validateCredentials | [UserId] User Not Found") {
        val input = UserCredentials(
            login = UserLogin("test--login"),
            password = "test--password",
        )

        val passwordHash = "test--password--hash"
        val dbResponse = GetPasswordHashResponse.Success(passwordHash)
        coEvery { usersRepoMock.getPasswordHash(input.login) } returns dbResponse
        every { hashingMock.isEqual(input.password, passwordHash) } returns true
        val userIdResponse = UserIdRepoResponse.UserNotFound
        coEvery { usersRepoMock.getUserId(input.login) } returns userIdResponse

        val expected = UserAuthInfo.Invalid(listOf("Password doesn't match"))
        val actual = shouldThrow<ServiceException.UserLoginNotFound> {
            userService.getUserAuthInfo(input)
        }
        actual shouldBe expected
    }
})
