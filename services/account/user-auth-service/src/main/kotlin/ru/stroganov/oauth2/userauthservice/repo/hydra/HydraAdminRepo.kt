package ru.stroganov.oauth2.userauthservice.repo.hydra

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.stroganov.oauth2.userauthservice.controller.response.GetConsentResponse
import ru.stroganov.oauth2.userauthservice.repo.hydra.response.AcceptConsentRepoResponse
import ru.stroganov.oauth2.userauthservice.repo.hydra.response.GetConsentRepoResponse
import ru.stroganov.oauth2.userauthservice.repo.hydra.response.LoginRequestResponse
import sh.ory.hydra.ApiCallback
import sh.ory.hydra.ApiException
import sh.ory.hydra.api.AdminApi
import sh.ory.hydra.model.AcceptConsentRequest
import sh.ory.hydra.model.AcceptLoginRequest
import sh.ory.hydra.model.ConsentRequest
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface HydraAdminRepo {
    suspend fun getLoginRequest(loginRequestId: String): LoginRequestResponse
    suspend fun acceptLogin(loginRequestId: String, subject: String): String
    suspend fun getConsent(consentChallenge: String): GetConsentRepoResponse
    suspend fun acceptConsent(consentChallenge: String): AcceptConsentRepoResponse
}

@Service
class HydraAdminRepoImpl(
    private val hydraAdminClient: AdminApi
) : HydraAdminRepo {
    private val log = KotlinLogging.logger {  }

    override suspend fun getLoginRequest(loginRequestId: String): LoginRequestResponse = suspendCoroutine { cont ->
        hydraAdminClient.getLoginRequestAsync(loginRequestId, cont.hydraCallback { LoginRequestResponse(
            it.challenge,
        ) }).also { log.info { it } }
    }

    override suspend fun acceptLogin(loginRequestId: String, subject: String): String = suspendCoroutine { cont ->
        val acceptLoginRequest = AcceptLoginRequest().apply {
            subject(subject)
        }
        hydraAdminClient.acceptLoginRequestAsync(loginRequestId, acceptLoginRequest, cont.hydraCallback {
            it.redirectTo
        }).also { log.info { it } }
    }

    override suspend fun getConsent(consentChallenge: String): GetConsentRepoResponse = suspendCoroutine { cont ->
        hydraAdminClient.getConsentRequestAsync(consentChallenge, cont.hydraCallback {
            GetConsentRepoResponse(
                requestedAccessTokenAudience = it.requestedAccessTokenAudience,
                requestedScope = it.requestedScope,
                subject = it.subject,
            )
        })
    }

    override suspend fun acceptConsent(consentChallenge: String): AcceptConsentRepoResponse = suspendCoroutine { cont ->
        val request = AcceptConsentRequest()
        hydraAdminClient.acceptConsentRequestAsync(consentChallenge, request, cont.hydraCallback {
            AcceptConsentRepoResponse(it.redirectTo)
        })
    }
}

private fun <T, R> Continuation<R>.hydraCallback(map: (T) -> R): ApiCallback<T> = object : ApiCallback<T> {
    private val log = KotlinLogging.logger {  }

    override fun onFailure(
        e: ApiException,
        statusCode: Int,
        responseHeaders: MutableMap<String, MutableList<String>>?
    ) {
        resumeWithException(e)
    }

    override fun onUploadProgress(bytesWritten: Long, contentLength: Long, done: Boolean) {
        log.info { "onUploadProgress: $bytesWritten" }
    }

    override fun onDownloadProgress(bytesRead: Long, contentLength: Long, done: Boolean) {
        log.info { "onDownloadProgress: $bytesRead" }
    }

    override fun onSuccess(result: T, statusCode: Int, responseHeaders: MutableMap<String, MutableList<String>>?) {
        resume(map(result))
    }
}