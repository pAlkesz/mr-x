package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.model.question.Answer
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.util.extensions.isGuessed

class GuessQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository,
) {

    suspend fun run(questionId: String, firstName: String, lastName: String?) = runCatching {
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
        (if (isHost) {
            val status =
                if (isGuessed) QuestionStatus.GUESSED_BY_HOST else QuestionStatus.WAITING_FOR_OWNER
            questionRepository.updateHostAnswer(id = questionId, answer = answer, status = status)
        } else {
            val status =
                if (isGuessed) QuestionStatus.GUESSED_BY_PLAYER else QuestionStatus.MISSED_BY_PLAYER
            questionRepository.updatePlayerAnswer(id = questionId, answer = answer, status = status)
        }).getOrThrow()
    }

    companion object {
        private const val NO_QUESTION_FOUND_ERROR_MESSAGE = "NO_QUESTION_FOUND"
        private const val NO_GAME_FOUND_ERROR_MESSAGE = "NO_GAME_FOUND"
    }
}
