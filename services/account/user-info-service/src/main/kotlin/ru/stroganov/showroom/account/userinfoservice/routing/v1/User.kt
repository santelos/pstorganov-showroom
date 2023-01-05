package ru.stroganov.showroom.account.userinfoservice.routing.v1

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.stroganov.showroom.account.userinfoservice.common.RoleRule
import ru.stroganov.showroom.account.userinfoservice.common.pathAuthorize
import ru.stroganov.showroom.account.userinfoservice.common.roleAuthorize
import ru.stroganov.showroom.account.userinfoservice.config.principalToRolesMapping
import ru.stroganov.showroom.account.userinfoservice.config.principalToUserId
import ru.stroganov.showroom.account.userinfoservice.routing.v1.model.*
import ru.stroganov.showroom.account.userinfoservice.service.UserService
import ru.stroganov.showroom.account.userinfoservice.service.UserServiceObject

fun Application.userModule(userService: UserService = UserServiceObject) {
    routing {
        authenticate("main") {
            route("/v1/user") {
                pathAuthorize("/{id}", ::principalToUserId) {
                    roleAuthorize(rule = RoleRule.HasRoles(setOf("user:GetInfo")), ::principalToRolesMapping) {
                        get("/info") {
                            val request = UserIdRequest(call.parameters.getOrFail("id"))
                            val result = userService.getUserInfo(request.toUserId())
                            call.respond(result.toResponse())
                        }
                    }
                }
                roleAuthorize(rule = RoleRule.HasRoles(setOf("user:VerifyCredentials")), ::principalToRolesMapping) {
                    post("/credentials") {
                        val request = call.receive<VerifyCredentialsRequest>()
                        val result = userService.validateCredentials(request.toService())
                        call.respond(result.toResponse())
                    }
                }
                roleAuthorize(rule = RoleRule.HasRoles(setOf("user:CreateUser")), ::principalToRolesMapping) {
                    post {
                        val newUserRequest = call.receive<NewUserRequest>()
                        val result = userService.createUser(newUserRequest.toService())
                        call.respond(result.toResponse())
                    }
                }
            }
        }
    }
}
