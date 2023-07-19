package ru.stroganov.account.userauthservice.web.v1

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import ru.stroganov.account.userauthservice.web.config.serverConfig
import ru.stroganov.account.userauthservice.web.config.statusPagesConfig

internal fun test(
    appBlock: Application.() -> Unit,
    block: suspend ApplicationTestBuilder.() -> Unit
) = testApplication {
    application {
        serverConfig()
        statusPagesConfig()
        appBlock()
    }
    block()
}

internal fun ApplicationTestBuilder.createClient(): HttpClient = createClient {
    install(ContentNegotiation) {
        json()
    }
}
