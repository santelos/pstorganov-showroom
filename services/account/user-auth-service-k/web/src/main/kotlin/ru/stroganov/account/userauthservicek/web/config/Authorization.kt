package ru.stroganov.account.userauthservicek.web.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

private class AuthorizationInterceptorsConfig {
    var interceptors: List<AuthorizationInterceptor> = emptyList()
}

private val AuthorizationInterceptors = createRouteScopedPlugin(
    name = "AuthorizationInterceptors",
    createConfiguration = ::AuthorizationInterceptorsConfig
) {
    on(AuthorizationHook) { call ->
        val authorizations = pluginConfig.interceptors
        val principal = call.authentication.principal<Principal>()
        if (principal == null) {
            log.warn { "Authorization is requested, but no principal found in application. Returning Unauthorized" }
            call.respond(HttpStatusCode.Unauthorized)
        } else if (authorizations.any { !call.authorized(it, principal) }) {
            log.trace { "One of the authorization interceptors is FALSE. Returning Forbidden" }
            call.respond(HttpStatusCode.Forbidden)
        }
    }
}

private object AuthorizationHook : Hook<suspend (ApplicationCall) -> Unit> {
    private val RoleBasedAuthorizationPhase: PipelinePhase = PipelinePhase("Authorization")

    override fun install(pipeline: ApplicationCallPipeline, handler: suspend (ApplicationCall) -> Unit) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Plugins, RoleBasedAuthorizationPhase)
        pipeline.intercept(RoleBasedAuthorizationPhase) { handler(call) }
    }
}

private fun ApplicationCall.authorized(auth: AuthorizationInterceptor, principal: Principal): Boolean {
    val verifiable = auth.principalToVerifiable(principal)
    log.trace { "Attempting authorization: [${auth.name}]. Verifiable: [$verifiable]" }
    if (verifiable == null) {
        log.trace { "Authorization verifiable is null, returning FALSE" }
        return false
    } else if (!auth.isVerified(this, verifiable)) {
        log.trace { "Authorization unsuccessful, executing FailureHandler, returning FALSE" }
        auth.failureHandler(this, verifiable)
        return false
    }
    return true
}

internal fun Route.roleAuthorize(
    rule: RoleRule,
    mapToRoles: (Principal) -> Set<String>?,
    build: Route.() -> Unit
): Route {
    val authorizedRoute = createChild(RoleBasedAuthorizationRouteSelector())
    return authorizedRoute.authorize(
        AuthorizationInterceptor(
            name = "RoleAuthorization",
            principalToVerifiable = mapToRoles,
            isVerified = { rule.authorized(it) },
            failureHandler = {
                log.debug { "Authorization did not pass role verification. Principal role: [$it]" }
            }
        ),
        build
    )
}

internal sealed interface RoleRule {
    object DenyAll : RoleRule
    data class HasRoles(val roles: Set<String>) : RoleRule
}

private fun RoleRule.authorized(verifiable: Set<String>): Boolean = when (this) {
    RoleRule.DenyAll -> false
    is RoleRule.HasRoles -> roles.intersect(verifiable).isNotEmpty()
}

internal fun Route.pathAuthorize(
    pathVariable: String,
    mapTo: (Principal) -> String?,
    build: Route.() -> Unit
): Route {
    val childRoute = createRouteFromPath(pathVariable)
    val parsedPathParam = RoutingPath.parse(pathVariable)
        .parts.single().value
        .removePrefix("{").removeSuffix("}")
    return childRoute.authorize(
        AuthorizationInterceptor(
            name = "PathVariableAuthorization",
            principalToVerifiable = { mapTo(it)?.let(::setOf) },
            isVerified = {
                val resolvedPathParam = parameters.getOrFail(parsedPathParam)
                log.trace { "Path var: [$resolvedPathParam]" }
                it.contains(resolvedPathParam)
            },
            failureHandler = {
                val resolvedPathParam = parameters.getOrFail(parsedPathParam)

                log.debug {
                    "Authorization did not pass path verification. " +
                        "Path: [${request.path()}], " +
                        "Variable: [$parsedPathParam]. " +
                        "Required: [$resolvedPathParam], " +
                        "Got: [$it]"
                }
            }
        ),
        build
    )
}

internal fun Route.authorize(authReg: AuthorizationInterceptor, build: Route.() -> Unit): Route {
    attributes.put(authorizationInterceptorAttributeKey, authReg)
    val allAuthorizations = generateSequence(this) { it.parent }
        .mapNotNull { it.attributes.getOrNull(authorizationInterceptorAttributeKey) }
        .toList()
        .reversed()
    install(AuthorizationInterceptors) {
        interceptors = allAuthorizations
    }
    return apply(build)
}

private class RoleBasedAuthorizationRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
        RouteSelectorEvaluation.Transparent
}

internal data class AuthorizationInterceptor(
    val name: String,
    val principalToVerifiable: (Principal) -> Set<String>?,
    val isVerified: ApplicationCall.(Set<String>) -> Boolean,
    val failureHandler: ApplicationCall.(Set<String>) -> Unit
)

private val authorizationInterceptorAttributeKey =
    AttributeKey<AuthorizationInterceptor>("AuthorizationInterceptor")
