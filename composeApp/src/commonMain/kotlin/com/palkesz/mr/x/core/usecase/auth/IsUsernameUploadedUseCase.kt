package com.palkesz.mr.x.core.usecase.auth

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.util.extensions.isNotNullOrBlank

fun interface IsUsernameUploadedUseCase {
    suspend fun run(): Boolean
}

class IsUsernameUploadedUseCaseImpl(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : IsUsernameUploadedUseCase {

    override suspend fun run(): Boolean {
        val userId = authRepository.userId ?: return false
        return authRepository.username.isNotNullOrBlank()
                && userRepository.fetchUser(userId).getOrNull()?.name.isNotNullOrBlank()
    }

}
