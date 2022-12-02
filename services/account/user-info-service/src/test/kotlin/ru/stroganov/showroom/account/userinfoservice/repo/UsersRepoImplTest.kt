package ru.stroganov.showroom.account.userinfoservice.repo

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerExtension
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitFirst
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import ru.stroganov.showroom.account.userinfoservice.service.UserCredentials
import ru.stroganov.showroom.account.userinfoservice.service.UserId
import ru.stroganov.showroom.account.userinfoservice.service.UserLogin

@Testcontainers
internal class UsersRepoImplTest : FunSpec({

    val postgresContainer = PostgreSQLContainer("postgres:15")
        .withUsername("postgres")
        .withPassword("postgres")
        .withDatabaseName("postgres")

    val ds = install(TestContainerExtension(postgresContainer))
    val (usersRepo, connectionFactory) = createTestable(ds)

    beforeSpec { migration(ds) }
    afterTest { connectionFactory.truncate("users") }

    test("getUserInfo | Success") {
        val name = "test--name"
        val userId = connectionFactory.createUser(login = name)
        val expected = UserInfoRepoResponse.Success(userId, name)
        val actual = usersRepo.getUserInfo(UserId(userId))
        actual shouldBe expected
    }

    test("getUserInfo | UserNotFound") {
        val userId = connectionFactory.createUser()
        val expected = UserInfoRepoResponse.UserNotFound
        val actual = usersRepo.getUserInfo(UserId(userId + 1))
        actual shouldBe expected
    }

    test("getPasswordHash | Success") {
        val login = "test--login"
        val passwordHash = "test--password-hash"
        val expected = GetPasswordHashResponse.Success(passwordHash)
        connectionFactory.createUser(login = login, passwordHash = passwordHash)
        val actual = usersRepo.getPasswordHash(UserLogin(login))
        actual shouldBe expected
    }

    test("getPasswordHash | UserNotFound") {
        val login = "test--login"
        val passwordHash = "test--password-hash"
        val expected = GetPasswordHashResponse.UserNotFound
        connectionFactory.createUser(login = "$login-broken", passwordHash = passwordHash)
        val actual = usersRepo.getPasswordHash(UserLogin(login))
        actual shouldBe expected
    }

    test("createUser") {
        val createUserRepoRequest = CreateUserRepoRequest(
            login = "test--login",
            passwordHash = "test--password-hash",
            name = "test--name",
        )
        val userId = usersRepo.createUser(createUserRepoRequest)
        connectionFactory.create().awaitFirst()
            .createStatement("SELECT login, password_hash, name FROM users WHERE id=$1")
            .bind("$1", userId.id).execute().awaitFirst()
            .map { t, _ ->
                t.get("login", String::class.java) shouldBe createUserRepoRequest.login
                t.get("password_hash", String::class.java) shouldBe createUserRepoRequest.passwordHash
                t.get("name", String::class.java) shouldBe createUserRepoRequest.name
            }.awaitFirst()
    }
})
