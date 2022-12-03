package ru.stroganov.oauth2.userauthservice.config

import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authentication.ServerHttpBasicAuthenticationConverter
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepo
import ru.stroganov.oauth2.userauthservice.service.RemoteReactiveAuthenticationManager
import java.util.*


@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    @Order(1)
    fun configureOauth(http: ServerHttpSecurity): SecurityWebFilterChain = http {

        csrf {
            disable()
        }
        authorizeExchange {
            authorize("/metrics", hasAuthority("SCOPE_internal:metrics"))
        }
        securityMatcher(
            PathPatternParserServerWebExchangeMatcher("/metrics")
        )
        requestCache {
            requestCache = NoOpServerRequestCache.getInstance()
        }
        logout {
            disable()
        }
        oauth2ResourceServer {
            opaqueToken { }
        }
    }

    @Bean
    @Order(2)
    fun configurePermitted(http: ServerHttpSecurity): SecurityWebFilterChain = http {

        csrf {
            disable()
        }
        authorizeExchange {
            authorize("/registration/new", permitAll)
        }
        securityMatcher(
            OrServerWebExchangeMatcher(
                PathPatternParserServerWebExchangeMatcher("/registration/new")
            )
        )
        anonymous { }
        requestCache {
            requestCache = NoOpServerRequestCache.getInstance()
        }
        logout {
            disable()
        }
    }

    @Bean
    @Order(3)
    fun configureBasicUserAuth(
        http: ServerHttpSecurity,
        userInfoServiceRepo: UserInfoServiceRepo
    ): SecurityWebFilterChain = http {

        csrf {
            disable()
        }
        authorizeExchange {
            authorize(anyExchange, authenticated)
        }
        requestCache {
            requestCache = NoOpServerRequestCache.getInstance()
        }
        addFilterAt(authFilter(userInfoServiceRepo), SecurityWebFiltersOrder.AUTHENTICATION)
        logout {
            disable()
        }
    }

    fun loginPasswordManager(
        userInfoServiceRepo: UserInfoServiceRepo
    ): ReactiveAuthenticationManager {
        return RemoteReactiveAuthenticationManager(userInfoServiceRepo)
    }

    fun authFilter(
        userInfoServiceRepo: UserInfoServiceRepo
    ): AuthenticationWebFilter = AuthenticationWebFilter(loginPasswordManager(userInfoServiceRepo)).apply {
        setServerAuthenticationConverter(ServerHttpBasicAuthenticationConverter())
    }
}

