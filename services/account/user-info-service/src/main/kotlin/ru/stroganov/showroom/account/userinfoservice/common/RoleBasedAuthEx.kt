package ru.stroganov.showroom.account.userinfoservice.common

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

sealed interface RoleRule {
    object DenyAll : RoleRule
    data class HasRoles(val roles: Set<String>): RoleRule
}

class RoleRuleConfig {
    var principalToRoles: (Principal) -> Set<String> = { emptySet() }
    var roleRule: RoleRule = RoleRule.DenyAll
}

val RoleBasedAuthorizationRoute = createRouteScopedPlugin(
    name = "RoleBasedAuthorization",
    createConfiguration = ::RoleRuleConfig
) {
    on(RoleBasedAuthHook) { call ->
        val principal = call.authentication.principal<Principal>()
        if (principal == null) {
            call.respond(HttpStatusCode.Unauthorized)
        } else {
            val roles = pluginConfig.principalToRoles(principal)
            if(!pluginConfig.roleRule.authorized(roles)) {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

private object RoleBasedAuthHook : Hook<suspend (ApplicationCall) -> Unit> {
    private val RoleBasedAuthorizationPhase: PipelinePhase = PipelinePhase("RoleBasedAuthorization")

    override fun install(pipeline: ApplicationCallPipeline, handler: suspend (ApplicationCall) -> Unit) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Plugins, RoleBasedAuthorizationPhase)
        pipeline.intercept(RoleBasedAuthorizationPhase) { handler(call) }
        println()
    }
}

private fun RoleRule.authorized(actualRoles: Set<String>): Boolean = when(this) {
    RoleRule.DenyAll -> false
    is RoleRule.HasRoles -> roles.intersect(actualRoles).isNotEmpty()
}

fun Route.roleAuthorize(
    rule: RoleRule,
    mapToRoles: (Principal) -> Set<String>,
    build: Route.() -> Unit
): Route {
    val authorizedRoute = createChild(RoleBasedAuthorizationRouteSelector())
    authorizedRoute.install(RoleBasedAuthorizationRoute) {
        principalToRoles = mapToRoles
        roleRule = rule
    }
    authorizedRoute.build()
    return authorizedRoute
}

private class RoleBasedAuthorizationRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
        RouteSelectorEvaluation.Transparent
}
