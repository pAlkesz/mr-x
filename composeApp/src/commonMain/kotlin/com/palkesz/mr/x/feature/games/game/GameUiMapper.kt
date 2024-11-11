package com.palkesz.mr.x.feature.games.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.BarkochbaStatus
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.util.extensions.getName
import com.palkesz.mr.x.core.util.extensions.immutableMap
import com.palkesz.mr.x.core.util.extensions.immutableMapNotNull

class GameUiMapper(
    private val authRepository: AuthRepository,
) {

    fun mapViewState(result: GameResult, event: GameEvent?): GameViewState {
        val isHost = authRepository.userId == result.game.hostId
        return GameViewState(
            host = result.host.name,
            firstName = result.game.firstName,
            lastName = result.game.lastName,
            isHost = isHost,
            isGameOngoing = result.game.status == GameStatus.ONGOING,
            isAskQuestionButtonVisible = !isHost,
            isAskBarkochbaQuestionButtonVisible = result.isAskBarkochbaQuestionButtonVisible(),
            barkochbaQuestionCount = result.getBarkochbaQuestionCount(),
            questions = result.mapQuestions(isHost = isHost),
            barkochbaQuestions = result.mapBarkochbaQuestions(isHost = isHost),
            event = event,
        )
    }

    private fun GameResult.isAskBarkochbaQuestionButtonVisible() = barkochbaQuestions.any {
        it.status == BarkochbaStatus.IN_STORE && it.userId == authRepository.userId
    }

    private fun GameResult.getBarkochbaQuestionCount() = barkochbaQuestions.count {
        it.status == BarkochbaStatus.IN_STORE
    }

    private fun GameResult.getOwner(id: String) = players.first { it.id == id }.name

    private fun GameResult.mapQuestions(isHost: Boolean) = questions.immutableMap { question ->
        when {
            question.status == QuestionStatus.WAITING_FOR_HOST && isHost -> {
                QuestionItem.GuessAsHostItem(
                    id = question.id,
                    text = question.text,
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            question.status == QuestionStatus.WAITING_FOR_HOST -> {
                QuestionItem.WaitingForHostItem(
                    text = question.text,
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            question.status == QuestionStatus.WAITING_FOR_PLAYERS && isHost -> {
                QuestionItem.WaitingForPlayersItem(
                    text = question.text,
                    hostAnswer = question.hostAnswer?.getName(),
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            question.status == QuestionStatus.WAITING_FOR_PLAYERS -> {
                QuestionItem.GuessAsPlayerItem(
                    id = question.id,
                    text = question.text,
                    hostAnswer = question.hostAnswer?.getName(),
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            question.status == QuestionStatus.PLAYERS_WON -> {
                QuestionItem.PlayersWonItem(
                    text = question.text,
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            question.status == QuestionStatus.GUESSED_BY_HOST -> {
                QuestionItem.GuessedByHostItem(
                    text = question.text,
                    hostAnswer = question.hostAnswer?.getName() ?: "",
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            question.status == QuestionStatus.WAITING_FOR_OWNER && question.userId == authRepository.userId -> {
                QuestionItem.VerifyAsOwnerItem(
                    id = question.id,
                    text = question.text,
                    hostAnswer = question.hostAnswer?.getName() ?: "",
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            question.status == QuestionStatus.WAITING_FOR_OWNER -> {
                QuestionItem.WaitingForOwnerItem(
                    text = question.text,
                    hostAnswer = question.hostAnswer?.getName() ?: "",
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            question.status == QuestionStatus.GUESSED_BY_PLAYER -> {
                QuestionItem.GuessedByPlayerItem(
                    text = question.text,
                    answer = question.playerAnswer?.getName() ?: "",
                    hostAnswer = question.hostAnswer?.getName(),
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }

            else -> {
                QuestionItem.MissedByPlayerItem(
                    text = question.text,
                    answer = question.playerAnswer?.getName() ?: "",
                    hostAnswer = question.hostAnswer?.getName(),
                    number = question.number,
                    owner = getOwner(id = question.userId),
                )
            }
        }
    }

    private fun GameResult.mapBarkochbaQuestions(isHost: Boolean) =
        barkochbaQuestions.immutableMapNotNull { question ->
            when {
                question.status == BarkochbaStatus.ASKED && isHost -> {
                    BarkochbaItem.AnswerAsHostItem(
                        id = question.id,
                        text = question.text,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                    )
                }

                question.status == BarkochbaStatus.ASKED -> {
                    BarkochbaItem.WaitingForHostItem(
                        text = question.text,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                    )
                }

                question.status == BarkochbaStatus.ANSWERED -> {
                    BarkochbaItem.AnsweredByHostItem(
                        text = question.text,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                        answer = question.answer,
                    )
                }

                else -> null
            }
        }
}
