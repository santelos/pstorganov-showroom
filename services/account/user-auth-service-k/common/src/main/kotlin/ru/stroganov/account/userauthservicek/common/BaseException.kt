package ru.stroganov.account.userauthservicek.common

sealed class BaseException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause) {
    sealed class RepoException(msg: String, cause: Throwable? = null) : BaseException(msg, cause) {
        data class Oauth2TokenException(
            val clientId: String,
            val pubicUrl: String,
            val c: Throwable
        ) : RepoException(
            "Could not obtain Oauth2 token from Oauth2 server. " +
                "ClientId: [$clientId], " +
                "PublicUrl: [$pubicUrl]",
            c
        )

        data class CreateUserException(
            val login: String,
            val c: Throwable
        ) : RepoException(
            "Could not create user. " +
                "Login: [$login]",
            c
        )

        data class GetUserInfoException(
            val login: String,
            val c: Throwable
        ) : RepoException(
            "Could not retrieve user info. " +
                "Login: [$login]",
            c
        )

        data class GetLoginRequestException(
            val loginRequestId: String,
            val c: Throwable
        ) : RepoException(
            "Could not retrieve login request. " +
                "LoginRequestId: [$loginRequestId]",
            c
        )

        data class AcceptLoginException(
            val loginRequestId: String,
            val subject: String,
            val c: Throwable
        ) : RepoException(
            "Could not accept login request. " +
                "LoginRequestId: [$loginRequestId], " +
                "Subject: [$subject]",
            c
        )

        data class GetConsentException(
            val consentChallenge: String,
            val c: Throwable
        ) : RepoException(
            "Could not retrieve consent challenge. " +
                "ConsentChallenge: [$consentChallenge]",
            c
        )

        data class AcceptConsentException(
            val consentChallenge: String,
            val scopes: List<String>,
            val c: Throwable
        ) : RepoException(
            "Could not accept consent challenge. " +
                "ConsentChallenge: [$consentChallenge], " +
                "Scopes: [$scopes]",
            c
        )
    }
}
