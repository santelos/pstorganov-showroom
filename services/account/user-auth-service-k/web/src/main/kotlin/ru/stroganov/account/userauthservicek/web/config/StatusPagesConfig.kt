package ru.stroganov.account.userauthservicek.web.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import mu.KotlinLogging

internal fun Application.statusPagesConfig() {
    val log = KotlinLogging.logger {  }

    install(StatusPages) {
        exception<Throwable> { call: ApplicationCall, cause: Throwable ->
            log.error(cause)
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "")
        }
    }
}
