package ru.stroganov.oauth2.userauthservice.exception.authentication

import org.springframework.security.core.AuthenticationException

data class UserAuthFailedException(
    val reasons: List<String>
) : AuthenticationException("Authentication failed. Reasons: [${reasons.joinToString()}]")
