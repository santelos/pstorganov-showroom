package ru.stroganov.showroom.account.userinfoservice.repo

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerExtension
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitFirst
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import ru.stroganov.showroom.account.userinfoservice.service.UserId

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

    test("getUserInfo") {
        val name = "test--name"
        val userId = connectionFactory.createUser(login = name)
        val expected = UserInfoRepoResponse(userId, name)
        val actual = usersRepo.getUserInfo(UserId(userId))
        expected shouldBe actual
    }

    test("getUserCredentials") {
        val expected = UserCredentialsRepoResponse("test--login", "test--password-hash")
        val userId = connectionFactory.createUser(login = expected.login, passwordHash = expected.passwordHash)
        val actual = usersRepo.getUserCredentials(UserId(userId))
        expected shouldBe actual
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
