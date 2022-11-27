package ru.stroganov.oauth2.userauthservice.config

import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenReactiveAuthenticationManager
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authentication.ServerHttpBasicAuthenticationConverter
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache
import org.springframework.web.server.ServerWebExchange

@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun configure(http: ServerHttpSecurity,
                  authFilter: AuthenticationWebFilter
    ): SecurityWebFilterChain = http {

        authorizeExchange {
            authorize(anyExchange, authenticated)
        }
        requestCache {
            requestCache = NoOpServerRequestCache.getInstance()
        }
        addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
    }

    @Bean
    fun resolver(
        @Qualifier("LoginPassword") loginPasswordManager: ReactiveAuthenticationManager,
        @Qualifier("Oauth2") oauth2Manager: ReactiveAuthenticationManager
    ): ReactiveAuthenticationManagerResolver<ServerWebExchange> = ReactiveAuthenticationManagerResolver {
        mono {
            if (it.request.path.pathWithinApplication().value().startsWith("/metrics")) {
                oauth2Manager
            } else {
                loginPasswordManager
            }
        }
    }

    @Bean
    @Qualifier("Oauth2")
    fun oauth2Manager(introspector: ReactiveOpaqueTokenIntrospector): ReactiveAuthenticationManager {
        return OpaqueTokenReactiveAuthenticationManager(introspector)
    }

    @Bean
    fun authFilter(
        authManagerResolver: ReactiveAuthenticationManagerResolver<ServerWebExchange>,
        authConverter: ServerAuthenticationConverter,
    ): AuthenticationWebFilter = AuthenticationWebFilter(authManagerResolver).apply {
        setServerAuthenticationConverter(authConverter)
    }

    @Bean
    fun authConverter(): ServerAuthenticationConverter = ServerHttpBasicAuthenticationConverter()
}
