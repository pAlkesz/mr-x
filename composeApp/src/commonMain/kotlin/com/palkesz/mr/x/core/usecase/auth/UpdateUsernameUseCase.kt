package com.palkesz.mr.x.core.usecase.auth

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.model.user.User
import com.palkesz.mr.x.core.util.networking.combineResult

class UpdateUsernameUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {

    suspend fun run(username: String): Result<Unit> {
        val id = authRepository.userId ?: return Result.failure(Throwable(NO_USER_ID_FOUND_MESSAGE))
        val authResult = authRepository.updateUsername(name = username)
        val userResult = userRepository.uploadUser(user = User(id = id, name = username))
        return combineResult(first = authResult, second = userResult, transform = { _, _ -> })
    }

}
