package ru.stroganov.oauth2.userauthservice.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.stroganov.oauth2.userauthservice.controller.request.RegistrationNewRequest
import ru.stroganov.oauth2.userauthservice.controller.request.toService
import ru.stroganov.oauth2.userauthservice.controller.response.UserIdResponse
import ru.stroganov.oauth2.userauthservice.controller.response.toResponse
import ru.stroganov.oauth2.userauthservice.service.RegistrationService

@RestController()
class Registration(
    private val registrationService: RegistrationService
) {

    @PostMapping("/registration/new")
    suspend fun new(@RequestBody request: RegistrationNewRequest): UserIdResponse {
        val result = registrationService.new(request.toService())
        return result.toResponse()
    }
}