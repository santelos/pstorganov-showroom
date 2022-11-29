package ru.stroganov.showroom.account.userinfoservice.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
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

    test("validateCredentials | Valid") {
        val input = UserCredentials(
            login = "test--login",
            password = "test--password",
        )
        val expected = UserCredentialsValidation.Valid
        val passwordHash = "test--password--hash"
        val dbResponse = GetPasswordHashResponse.Success(
            passwordHash = passwordHash,
        )
        coEvery { usersRepoMock.getPasswordHash(UserLogin(input.login)) } returns dbResponse
        every { hashingMock.isEqual(input.password, passwordHash) } returns true
        val actual = userService.validateCredentials(input)
        actual shouldBe expected
    }

    test("validateCredentials | Invalid | User not found") {
        val input = UserCredentials(
            login = "test--login",
            password = "test--password",
        )
        val expected = UserCredentialsValidation.Invalid(listOf("User not found"))
        val dbResponse = GetPasswordHashResponse.UserNotFound
        coEvery { usersRepoMock.getPasswordHash(UserLogin(input.login)) } returns dbResponse
        every { hashingMock.isEqual(input.password, any()) } returns true
        val actual = userService.validateCredentials(input)
        actual shouldBe expected
    }

    test("validateCredentials | Invalid | Password doesn't match") {
        val input = UserCredentials(
            login = "test--login",
            password = "test--password",
        )
        val expected = UserCredentialsValidation.Invalid(listOf("Password doesn't match"))
        val passwordHash = "test--password--hash"
        val dbResponse = GetPasswordHashResponse.Success(passwordHash)
        coEvery { usersRepoMock.getPasswordHash(UserLogin(input.login)) } returns dbResponse
        every { hashingMock.isEqual(input.password, passwordHash) } returns false
        val actual = userService.validateCredentials(input)
        actual shouldBe expected
    }
})
