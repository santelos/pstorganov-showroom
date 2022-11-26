package ru.stroganov.showroom.account.userinfoservice.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import ru.stroganov.showroom.account.userinfoservice.repo.CreateUserRepoRequest
import ru.stroganov.showroom.account.userinfoservice.repo.UserCredentialsRepoResponse
import ru.stroganov.showroom.account.userinfoservice.repo.UserInfoRepoResponse
import ru.stroganov.showroom.account.userinfoservice.repo.UsersRepo

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
        val dbResponse = UserInfoRepoResponse(
            id = expected.id,
            name = expected.name,
        )
        coEvery { usersRepoMock.getUserInfo(input) } returns dbResponse
        val actual = userService.getUserInfo(input)
        expected shouldBe actual
    }

    test("getUserCredentials") {
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
        expected shouldBe actual
    }
})
