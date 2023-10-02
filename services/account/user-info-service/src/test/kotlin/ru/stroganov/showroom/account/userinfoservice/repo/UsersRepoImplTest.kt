package ru.stroganov.showroom.account.userinfoservice.repo

import kotlin.test.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.stroganov.showroom.account.userinfoservice.routing.v1.model.UserIdResponse
import ru.stroganov.showroom.account.userinfoservice.service.UserId
import ru.stroganov.showroom.account.userinfoservice.service.UserLogin

@Testcontainers
internal class UsersRepoImplTest {

    companion object {
        @Container
        val mockServer = PostgreSQLContainer("postgres:15").apply {
            withUsername("postgres")
            withPassword("postgres")
            withDatabaseName("postgres")
        }

        @JvmStatic
        @BeforeAll
        fun beforeTest() {
            migration(mockServer)
        }
    }
    private val cr: ConnectionAndRepo by lazy {
        createTestable(mockServer)
    }

    @AfterTest
    fun afterTest() = runBlocking {
        cr.connectionFactory.truncate("users")
    }

    @Test
    fun `getUserId ~~ Success`() = runBlocking {
        val login = "test--login"
        val userId = cr.connectionFactory.createUser(login = login)
        val expected = UserIdRepoResponse.Success(UserId(userId))
        val actual = cr.repo.getUserId(UserLogin(login))
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserId ~~ UserNotFound`() = runBlocking {
        val login = "test--login"
        cr.connectionFactory.createUser(login = login)
        val expected = UserIdRepoResponse.UserNotFound
        val actual = cr.repo.getUserId(UserLogin("$login--broken"))
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserAuthInfo ~~ Success`() = runBlocking {
        val passwordHash = "test--password-hash"
        val roles = setOf("test:role")
        val userId = cr.connectionFactory.createUser(passwordHash = passwordHash, roles = roles)
        val expected = UserAuthInfoRepoResponse.Success(passwordHash, roles)
        val actual = cr.repo.getUserAuthInfo(UserId(userId))
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserAuthInfo ~~ UserNotFound`() = runBlocking {
        val userId = cr.connectionFactory.createUser()
        val expected = UserAuthInfoRepoResponse.UserNotFound
        val actual = cr.repo.getUserAuthInfo(UserId(userId + 1))
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserPublicInfo ~~ Success`() = runBlocking {
        val name = "test--name"
        val userId = cr.connectionFactory.createUser(name = name)
        val expected = UserPublicInfoRepoResponse.Success(name)
        val actual = cr.repo.getUserPublicInfo(UserId(userId))
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserPublicInfo ~~ UserNotFound`() = runBlocking {
        val userId = cr.connectionFactory.createUser()
        val expected = UserPublicInfoRepoResponse.UserNotFound
        val actual = cr.repo.getUserPublicInfo(UserId(userId + 1))
        assertEquals(expected, actual)
    }

    @Test
    fun `createUser ~~ Success`() = runBlocking {
        val createUserRepoRequest = CreateUserRepoRequest(
            login = "test--login",
            passwordHash = "test--password-hash",
            name = "test--name",
            roles = setOf("test:role")
        )
        val userId = cr.repo.createUser(createUserRepoRequest)
        cr.connectionFactory.create().awaitFirst()
            .createStatement("SELECT login, password_hash, name, roles FROM users WHERE id=$1")
            .bind("$1", userId.id).execute().awaitFirst()
            .map { t, _ ->
                assertEquals(createUserRepoRequest.login, t.get("login", String::class.java))
                assertEquals(createUserRepoRequest.passwordHash, t.get("password_hash", String::class.java))
                assertEquals(createUserRepoRequest.name, t.get("name", String::class.java))
                assertEquals(createUserRepoRequest.roles, t.get("roles", Array<String>::class.java)!!.toSet())
            }.awaitFirst() as Unit
    }

}
