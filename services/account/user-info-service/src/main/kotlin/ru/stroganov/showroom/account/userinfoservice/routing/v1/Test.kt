package ru.stroganov.showroom.account.userinfoservice.routing.v1

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.stroganov.showroom.account.userinfoservice.common.Introspection
import ru.stroganov.showroom.account.userinfoservice.common.RoleRule
import ru.stroganov.showroom.account.userinfoservice.common.roleAuthorize
import ru.stroganov.showroom.account.userinfoservice.config.principalToRolesMapping

fun Application.testModule() {
    routing {
        authenticate("main") {
            roleAuthorize(rule = RoleRule.HasRoles(setOf("asd:test")), ::principalToRolesMapping) {
                get("/test") {
                    val principal = call.authentication.principal as Introspection
                    call.respond("Hello ${principal.username}, authorized: [${principal.scope}]")
                }
            }
        }
    }
}