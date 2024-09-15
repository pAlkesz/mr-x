package com.mr.x.core.usecase.game

import com.mr.x.core.data.user.UserRepository
import com.mr.x.core.model.GameWithHost
import com.mr.x.core.util.flatMapResult
import com.mr.x.core.util.mapResult

class GetGameWithHostUseCase(
	private val userRepository: UserRepository,
	private val getAndObserveGameUseCase: GetAndObserveGameUseCase
) {

	fun run(gameId: String) = getAndObserveGameUseCase.run(gameId).flatMapResult { game ->
		userRepository.getUser(game.hostId).mapResult {
			GameWithHost(game = game, host = it)
		}
	}
}
