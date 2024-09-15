package com.mr.x.core.usecase.game

import com.mr.x.core.data.game.GameRepository
import com.mr.x.core.data.user.AuthRepository
import com.mr.x.core.model.game.Game
import com.mr.x.core.model.game.GameStatus
import com.mr.x.core.util.CoroutineHelper
import com.mr.x.core.util.randomUUID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CreateGameUseCase(
	private val gameRepository: GameRepository,
	private val authRepository: AuthRepository
) {
	fun createGame(firstName: String, lastName: String) {
		authRepository.currentUserId?.let {
			CoroutineHelper.ioScope.launch {
				gameRepository.uploadGame(
					Game(
						uuid = randomUUID(),
						firstName = firstName,
						lastName = lastName,
						hostId = it,
						status = GameStatus.ONGOING
					)
				).collect()
			}
		}
	}
}
