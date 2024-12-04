package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.model.question.Answer
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.util.extensions.isGuessed

fun interface GuessQuestionUseCase {
    suspend fun run(questionId: String, firstName: String, lastName: String?): Result<Unit>
}

class GuessQuestionUseCaseImpl(
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository,
    private val createBarkochbaQuestionUseCase: CreateBarkochbaQuestionUseCase,
) : GuessQuestionUseCase {

    override suspend fun run(questionId: String, firstName: String, lastName: String?) =
        runCatching {
            val userId = authRepository.userId ?: throw Throwable(NO_USER_ID_FOUND_MESSAGE)
            val question = questionRepository.questions.value.find { question ->
                question.id == questionId
            } ?: throw Throwable(NO_QUESTION_FOUND_ERROR_MESSAGE)
            val game = gameRepository.games.value.find { game ->
                game.id == question.gameId
            } ?: throw Throwable(NO_GAME_FOUND_ERROR_MESSAGE)
            val answer =
                Answer(firstName = firstName.trim(), lastName = lastName?.trim(), userId = userId)
            val isGuessed = answer.isGuessed(
                firstName = question.expectedFirstName,
                lastName = question.expectedLastName,
            )
            val isHost = game.hostId == userId
            when {
                isHost && isGuessed -> {
                    questionRepository.updateHostAnswer(
                        id = questionId,
                        answer = answer,
                        status = QuestionStatus.GUESSED_BY_HOST,
                    ).getOrThrow()
                }

                isHost -> {
                    questionRepository.updateHostAnswer(
                        id = questionId,
                        answer = answer,
                        status = QuestionStatus.WAITING_FOR_OWNER,
                    ).getOrThrow()
                }

                isGuessed -> {
                    questionRepository.updatePlayerAnswer(
                        id = questionId,
                        answer = answer,
                        status = QuestionStatus.GUESSED_BY_PLAYER,
                    ).getOrThrow()
                    createBarkochbaQuestionUseCase.run(gameId = game.id).getOrThrow()
                }

                else -> {
                    questionRepository.updatePlayerAnswer(
                        id = questionId,
                        answer = answer,
                        status = QuestionStatus.MISSED_BY_PLAYER,
                    ).getOrThrow()
                }
            }
        }

    companion object {
        private const val NO_QUESTION_FOUND_ERROR_MESSAGE = "NO_QUESTION_FOUND"
        private const val NO_GAME_FOUND_ERROR_MESSAGE = "NO_GAME_FOUND"
    }
}
