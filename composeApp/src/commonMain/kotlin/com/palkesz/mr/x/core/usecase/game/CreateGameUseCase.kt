package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.util.coroutines.CoroutineHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class CreateGameUseCase(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) {
    fun createGame(firstName: String, lastName: String) {
        authRepository.currentUserId?.let {
            CoroutineHelper.ioScope.launch {
                gameRepository.uploadGame(
                    Game(
                        uuid = Uuid.random().toString(),
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
