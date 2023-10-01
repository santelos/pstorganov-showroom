package ru.stroganov.showroom.account.userinfoservice.repo

import kotlin.test.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
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
    fun `getUserInfo ~~ Success`() = runBlocking {
        val name = "test--name"
        val userId = cr.connectionFactory.createUser(login = name)
        val roles = setOf("test:role")
        val expected = UserInfoRepoResponse.Success(userId, name, roles)
        val actual = cr.repo.getUserInfo(UserId(userId))
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserInfo ~~ UserNotFound`() = runBlocking {
        val userId = cr.connectionFactory.createUser()
        val expected = UserInfoRepoResponse.UserNotFound
        val actual = cr.repo.getUserInfo(UserId(userId + 1))
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserId ~~ Success`() = runBlocking {
        val name = "test--name"
        val userId = cr.connectionFactory.createUser(login = name)
        val expected = UserIdRepoResponse.Success(userId)
        val actual = cr.repo.getUserId(UserLogin(name))
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserId ~~ UserNotFound`() = runBlocking {
        val name = "test--name"
        cr.connectionFactory.createUser(login = name)
        val expected = UserIdRepoResponse.UserNotFound
        val actual = cr.repo.getUserId(UserLogin(name + "broken"))
        assertEquals(expected, actual)
    }

    @Test
    fun `getPasswordHash ~~ Success`() = runBlocking {
        val login = "test--login"
        val passwordHash = "test--password-hash"
        val expected = GetPasswordHashResponse.Success(passwordHash)
        cr.connectionFactory.createUser(login = login, passwordHash = passwordHash)
        val actual = cr.repo.getPasswordHash(UserLogin(login))
        assertEquals(expected, actual)
    }

    @Test
    fun `getPasswordHash ~~ UserNotFound`() = runBlocking {
        val login = "test--login"
        val passwordHash = "test--password-hash"
        val expected = GetPasswordHashResponse.UserNotFound
        cr.connectionFactory.createUser(login = "$login-broken", passwordHash = passwordHash)
        val actual = cr.repo.getPasswordHash(UserLogin(login))
        assertEquals(expected, actual)
    }

    @Test
    fun createUser() = runBlocking {
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
            }.awaitFirst()
    }

}
