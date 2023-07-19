package ru.stroganov.account.userauthservicek.service

import mu.KotlinLogging
import ru.stroganov.account.userauthservicek.common.UserId
import ru.stroganov.account.userauthservicek.repo.UserInfoServiceRepo
import ru.stroganov.account.userauthservicek.repo.UserInfoServiceRepoCreateUserRequest
import ru.stroganov.account.userauthservicek.repo.userInfoServiceRepoImpl

data class RegistrationNewServiceRequest(
    val login: String,
    val password: String,
    val name: String
)

interface RegistrationService {
    suspend fun new(request: RegistrationNewServiceRequest): UserId
}

val registrationServiceImpl: RegistrationService by lazy {
    RegistrationServiceImpl(userInfoServiceRepoImpl)
}
internal class RegistrationServiceImpl(
    private val userInfoServiceRepo: UserInfoServiceRepo
) : RegistrationService {
    private val log = KotlinLogging.logger { }

    override suspend fun new(request: RegistrationNewServiceRequest): UserId {
        log.info { "NewUser registration. Login: [${request.login}]" }
        val repoRequest = UserInfoServiceRepoCreateUserRequest(
            login = request.login,
            password = request.password,
            name = request.name
        )
        val result = userInfoServiceRepo.createUser(repoRequest)
        return UserId(result.userId)
    }
}
