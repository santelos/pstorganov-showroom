package ru.stroganov.account.userauthservicek.web.v1

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import ru.stroganov.account.userauthservicek.service.ConsentService
import ru.stroganov.account.userauthservicek.service.LoginService
import ru.stroganov.account.userauthservicek.web.config.BASIC_AUTH
import ru.stroganov.account.userauthservicek.web.config.OAUTH2_AUTH
import ru.stroganov.account.userauthservicek.web.config.oauth2ResourceServer
import kotlin.test.*

internal class OauthFlowKtTest {

    private val loginService: LoginService = mockk()
    private val consentService: ConsentService = mockk()
    private fun test(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            oauthFlow(loginService, consentService)
            testAuth()
        }
        block()
    }

    private val authMock: () -> Principal = mockk()
    private fun Application.testAuth() {
        install(Authentication) {
            basic(BASIC_AUTH) {
                validate { authMock() }
            }
        }
    }

//    @Test
//    fun oauthFlow() = test {
//    }
}
