package com.palkesz.mr.x.core.usecase.user

import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.model.User
import com.palkesz.mr.x.core.util.flatMapResult

class CreateUserUseCase(
	private val userRepository: UserRepository,
	private val authRepository: AuthRepository
) {
	fun run(userName: String, email: String, password: String) =
		authRepository.createUser(
			email = email,
			password = password,
			username = userName
		).flatMapResult {
			userRepository.uploadUser(User(userId = it, userName = userName))
		}

}
