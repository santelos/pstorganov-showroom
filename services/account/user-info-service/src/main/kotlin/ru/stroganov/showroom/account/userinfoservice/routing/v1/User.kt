package ru.stroganov.showroom.account.userinfoservice.routing.v1

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.stroganov.showroom.account.userinfoservice.common.RoleRule
import ru.stroganov.showroom.account.userinfoservice.common.roleAuthorize
import ru.stroganov.showroom.account.userinfoservice.config.principalToRolesMapping
import ru.stroganov.showroom.account.userinfoservice.routing.v1.model.*
import ru.stroganov.showroom.account.userinfoservice.service.*
import ru.stroganov.showroom.account.userinfoservice.service.UserServiceObject

fun Application.userModule(userService: UserService = UserServiceObject) {
    routing {
        authenticate("main") {
            route("/v1/user") {
                roleAuthorize(rule = RoleRule.HasRoles(setOf("user:GetUserId")), ::principalToRolesMapping) {
                    post("/id") {
                        val request = call.receive<UserIdRequest>()
                        val result = userService.getUserId(UserLogin(request.login))
                        call.respond(UserIdResponse(result.id))
                    }
                }
                roleAuthorize(rule = RoleRule.HasRoles(setOf("user:GetAuthInfo")), ::principalToRolesMapping) {
                    post("/auth-info") {
                        val request = call.receive<GetUserAuthInfoRequest>()
                        val result = userService.getUserAuthInfo(UserId(request.userId))
                        call.respond(GetUserAuthInfoResponse(result.passwordHash, result.roles))
                    }
                }
                roleAuthorize(rule = RoleRule.HasRoles(setOf("user:GetUserPublicInfo")), ::principalToRolesMapping) {
                    get("/{id}/info") {
                        val request = UserPublicInfoRequest(call.parameters.getOrFail("id").toInt())
                        val result = userService.getUserPublicInfo(UserId(request.id))
                        call.respond(UserPublicInfoResponse(result.name))
                    }
                }
                roleAuthorize(rule = RoleRule.HasRoles(setOf("user:CreateUser")), ::principalToRolesMapping) {
                    post {
                        val newUserRequest = call.receive<NewUserRequest>()
                        val result = userService.createUser(NewUser(
                            login = newUserRequest.login,
                            password = newUserRequest.password,
                            name = newUserRequest.name,
                            roles = newUserRequest.roles,
                        ))
                        call.respond(NewUserResponse(result.id))
                    }
                }
            }
        }
    }
}
