package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.Question
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.util.extensions.equalsAsName
import dev.gitlive.firebase.firestore.Timestamp
import kotlin.uuid.Uuid

fun interface CreateQuestionUseCase {
    suspend fun run(
        text: String,
        firstName: String,
        lastName: String?,
        gameId: String,
    ): Result<Question>
}

class CreateQuestionUseCaseImpl(
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository
) : CreateQuestionUseCase {

    override suspend fun run(text: String, firstName: String, lastName: String?, gameId: String) =
        runCatching {
            val userId = authRepository.userId ?: throw Throwable(NO_USER_ID_FOUND_MESSAGE)
            val game = gameRepository.games.value.first { it.id == gameId }
            val status =
                if (game.firstName.equalsAsName(firstName) &&
                    game.lastName.orEmpty().equalsAsName(lastName)
                ) {
                    QuestionStatus.PLAYERS_WON
                } else {
                    QuestionStatus.WAITING_FOR_HOST
                }
            if (status == QuestionStatus.PLAYERS_WON) {
                gameRepository.updateStatus(id = game.id, status = GameStatus.FINISHED).getOrThrow()
            }
            questionRepository.createQuestion(
                question = Question(
                    id = Uuid.random().toString(),
                    userId = userId,
                    gameId = gameId,
                    number = questionRepository.questions.value.filter { it.gameId == gameId }.size + 1,
                    expectedFirstName = firstName.trim(),
                    expectedLastName = lastName?.trim(),
                    hostAnswer = null,
                    playerAnswer = null,
                    text = text.trim(),
                    status = status,
                    lastModifiedTimestamp = Timestamp.now(),
                )
            ).getOrThrow()
        }

}
