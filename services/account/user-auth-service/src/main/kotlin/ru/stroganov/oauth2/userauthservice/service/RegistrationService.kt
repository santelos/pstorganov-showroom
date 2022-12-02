package ru.stroganov.oauth2.userauthservice.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepo
import ru.stroganov.oauth2.userauthservice.repo.userinfo.UserInfoServiceRepoCreateUserRequest
import ru.stroganov.oauth2.userauthservice.service.model.UserId

data class RegistrationNewServiceRequest(
    val login: String,
    val password: String,
    val name: String,
)

interface RegistrationService {
    suspend fun new(request: RegistrationNewServiceRequest): UserId
}

@Service
internal class RegistrationServiceImpl(
    private val userInfoServiceRepo: UserInfoServiceRepo,
) : RegistrationService {

    override suspend fun new(request: RegistrationNewServiceRequest): UserId {
        val repoRequest = UserInfoServiceRepoCreateUserRequest(
            login = request.login,
            password = request.password,
            name = request.name,
        )
        val result = userInfoServiceRepo.createUser(repoRequest)
        return UserId(result.userId)
    }
}