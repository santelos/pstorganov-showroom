package ru.stroganov.account.userauthservicek.web.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.statusPagesConfig() {
    install(StatusPages) {
        exception<Throwable> { call: ApplicationCall, cause: Throwable ->
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "")
        }
    }
}
