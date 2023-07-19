package ru.stroganov.account.userauthservice.repo

import mu.KotlinLogging
import ru.stroganov.account.userauthservice.common.BaseException.RepoException.*
import ru.stroganov.account.userauthservice.config.appConfig
import ru.stroganov.account.userauthservice.repo.config.hydraClient
import sh.ory.hydra.ApiCallback
import sh.ory.hydra.ApiException
import sh.ory.hydra.api.AdminApi
import sh.ory.hydra.model.AcceptConsentRequest
import sh.ory.hydra.model.AcceptLoginRequest
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class AcceptConsentRepoResponse(
    val redirectTo: String
)

data class GetConsentRepoResponse(
    val requestedAccessTokenAudience: List<String>?,
    val requestedScope: List<String>?,
    val subject: String?
)

data class LoginRequestResponse(
    val challenge: String
)

data class AcceptLoginRepoResponse(
    val redirectTo: String
)

interface HydraAdminRepo {
    suspend fun getLoginRequest(loginRequestId: String): LoginRequestResponse
    suspend fun acceptLogin(loginRequestId: String, subject: String): AcceptLoginRepoResponse
    suspend fun getConsent(consentChallenge: String): GetConsentRepoResponse
    suspend fun acceptConsent(consentChallenge: String, scopes: List<String>): AcceptConsentRepoResponse
}

val hydraAdminRepoImpl: HydraAdminRepo by lazy {
    HydraAdminRepoImpl(hydraClient(appConfig.hydra))
}
internal class HydraAdminRepoImpl(
    private val hydraAdminClient: AdminApi
) : HydraAdminRepo {
    private val log = KotlinLogging.logger { }

    override suspend fun getLoginRequest(loginRequestId: String): LoginRequestResponse = runCatching {
        suspendCoroutine { cont ->
            hydraAdminClient.getLoginRequestAsync(
                loginRequestId,
                cont.hydraCallback {
                    LoginRequestResponse(
                        it.challenge
                    )
                }
            )
        }
    }.getOrElse { throw GetLoginRequestException(loginRequestId, it) }

    override suspend fun acceptLogin(loginRequestId: String, subject: String): AcceptLoginRepoResponse = runCatching {
        suspendCoroutine { cont ->
            val acceptLoginRequest = AcceptLoginRequest().apply {
                subject(subject)
            }
            hydraAdminClient.acceptLoginRequestAsync(
                loginRequestId,
                acceptLoginRequest,
                cont.hydraCallback {
                    AcceptLoginRepoResponse(it.redirectTo)
                }
            )
        }
    }.getOrElse { throw AcceptLoginException(loginRequestId, subject, it) }

    override suspend fun getConsent(consentChallenge: String): GetConsentRepoResponse = runCatching {
        suspendCoroutine { cont ->
            hydraAdminClient.getConsentRequestAsync(
                consentChallenge,
                cont.hydraCallback {
                    GetConsentRepoResponse(
                        requestedAccessTokenAudience = it.requestedAccessTokenAudience,
                        requestedScope = it.requestedScope,
                        subject = it.subject
                    )
                }
            )
        }
    }.getOrElse { throw GetConsentException(consentChallenge, it) }

    override suspend fun acceptConsent(
        consentChallenge: String,
        scopes: List<String>
    ): AcceptConsentRepoResponse = runCatching {
        suspendCoroutine { cont ->
            val request = AcceptConsentRequest().apply {
                grantScope = scopes
            }
            hydraAdminClient.acceptConsentRequestAsync(
                consentChallenge,
                request,
                cont.hydraCallback {
                    AcceptConsentRepoResponse(it.redirectTo)
                }
            )
        }
    }.getOrElse { throw AcceptConsentException(consentChallenge, scopes, it) }
}

private fun <T, R> Continuation<R>.hydraCallback(map: (T) -> R): ApiCallback<T> = object : ApiCallback<T> {
    private val log = KotlinLogging.logger { }

    override fun onFailure(
        e: ApiException,
        statusCode: Int,
        responseHeaders: MutableMap<String, MutableList<String>>?
    ) {
        resumeWithException(e)
    }

    override fun onUploadProgress(bytesWritten: Long, contentLength: Long, done: Boolean) {
        log.debug { "onUploadProgress: $bytesWritten" }
    }

    override fun onDownloadProgress(bytesRead: Long, contentLength: Long, done: Boolean) {
        log.debug { "onDownloadProgress: $bytesRead" }
    }

    override fun onSuccess(result: T, statusCode: Int, responseHeaders: MutableMap<String, MutableList<String>>?) {
        resume(map(result))
    }
}
