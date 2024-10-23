package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.game.GameStatus
import dev.gitlive.firebase.firestore.Timestamp
import kotlin.uuid.Uuid

class CreateGameUseCase(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) {

    suspend fun run(firstName: String, lastName: String?) =
        authRepository.userId?.let { userId ->
            gameRepository.createGame(
                Game(
                    id = Uuid.random().toString(),
                    firstName = firstName,
                    lastName = lastName,
                    hostId = userId,
                    status = GameStatus.ONGOING,
                    lastModifiedTimeStamp = Timestamp.now(),
                )
            )
        } ?: Result.failure(exception = Throwable(NO_USER_ID_FOUND_MESSAGE))

}
