package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository

class UploadNormalQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository
) {

    /*fun run(text: String, firstName: String, lastName: String, gameId: String) {
        authRepository.userId?.let { userId ->
            CoroutineHelper.ioScope.launch {
                gameRepository.playerGames.value.find {
                    it.uuid == gameId
                }?.let { game ->
                    val status = if (game.firstName.trimmedEqualsIgnoreCase(firstName) &&
                        game.lastName.trimmedEqualsIgnoreCase(lastName)
                    ) {
                        Status.PLAYER_WON
                    } else {
                        Status.WAITING_FOR_HOST
                    }

                    if (status == Status.PLAYER_WON) {
                        gameRepository.updateStatus(
                            gameId = game.uuid,
                            status = GameStatus.FINISHED
                        ).collect()
                    }

                    questionRepository.uploadQuestion(
                        question = Question(
                            text = text.trim(),
                            expectedFirstName = firstName.trim(),
                            expectedLastName = lastName.trim(),
                            uuid = Uuid.random().toString(),
                            gameId = gameId,
                            status = status,
                            askerId = userId,
                            lastModifiedTS = Timestamp.now()
                        )
                    ).collect()
                }
            }
        }
    }*/
}
