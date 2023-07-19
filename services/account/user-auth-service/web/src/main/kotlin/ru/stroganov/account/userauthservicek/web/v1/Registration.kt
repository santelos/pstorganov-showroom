package ru.stroganov.account.userauthservice.web.v1

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ru.stroganov.account.userauthservice.service.RegistrationNewServiceRequest
import ru.stroganov.account.userauthservice.service.RegistrationService
import ru.stroganov.account.userauthservice.service.registrationServiceImpl

@Serializable
internal data class RegistrationNewRequest(
    val login: String,
    val password: String,
    val name: String
)

@Serializable
internal data class RegistrationNewResponse(
    val id: Int
)

internal fun Application.registration(
    registrationService: RegistrationService = registrationServiceImpl
) {
    routing {
        post("/registration/new") {
            val request = call.receive<RegistrationNewRequest>()
            val response = registrationService.new(
                RegistrationNewServiceRequest(
                    request.login,
                    request.password,
                    request.name
                )
            )
            call.respond(RegistrationNewResponse(response.id))
        }
    }
}
