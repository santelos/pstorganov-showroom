package ru.stroganov.showroom.account.userinfoservice.routing.v1

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.stroganov.showroom.account.userinfoservice.routing.v1.request.NewUserRequest
import ru.stroganov.showroom.account.userinfoservice.routing.v1.request.UserIdRequest
import ru.stroganov.showroom.account.userinfoservice.routing.v1.request.toService
import ru.stroganov.showroom.account.userinfoservice.routing.v1.request.toUserId
import ru.stroganov.showroom.account.userinfoservice.routing.v1.response.toUserInfoResponse
import ru.stroganov.showroom.account.userinfoservice.service.UserService
import ru.stroganov.showroom.account.userinfoservice.service.UserServiceObject

fun Application.userModule(userService: UserService = UserServiceObject) {
    routing {
        route("/v1/user") {
            route("/{id}") {
                route("/info") {
                    get {
                        val request = UserIdRequest(call.parameters.getOrFail("id"))
                        val result = userService.getUserInfo(request.toUserId())
                        call.respond(result.toUserInfoResponse())
                    }
                }
                route("/credentials") {
                    get { }
                }
            }
            post {
                val newUserRequest = call.receive<NewUserRequest>()
                val result = userService.createUser(newUserRequest.toService())
                call.respond(result)
            }
        }
    }
}
