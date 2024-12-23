package com.palkesz.mr.x.feature.games.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.BarkochbaStatus
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.util.extensions.getName
import com.palkesz.mr.x.core.util.extensions.immutableMap
import com.palkesz.mr.x.core.util.extensions.immutableMapNotNull
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.own_name_label
import org.jetbrains.compose.resources.getString

fun interface GameUiMapper {
    suspend fun mapViewState(result: GameResult, event: GameEvent?): GameViewState
}

class GameUiMapperImpl(
    private val authRepository: AuthRepository,
) : GameUiMapper {

    override suspend fun mapViewState(result: GameResult, event: GameEvent?): GameViewState {
        val isHost = authRepository.userId == result.game.hostId
        val isGameOngoing = result.game.status == GameStatus.ONGOING
        val hostName = result.getHostName(isHost = isHost)
        return GameViewState(
            host = result.host.name.takeIf { !isHost },
            firstName = result.game.firstName,
            lastName = result.game.lastName,
            isHost = isHost,
            isGameOngoing = result.game.status == GameStatus.ONGOING,
            isAskQuestionButtonVisible = !isHost && isGameOngoing,
            isAskBarkochbaQuestionButtonVisible = result.isAskBarkochbaQuestionButtonVisible(),
            barkochbaQuestionCount = result.getBarkochbaQuestionCount(),
            questions = result.mapQuestions(isHost = isHost, hostName = hostName),
            barkochbaQuestions = result.mapBarkochbaQuestions(isHost = isHost, hostName = hostName),
            event = event,
        )
    }

    private fun GameResult.isAskBarkochbaQuestionButtonVisible() = barkochbaQuestions.any {
        it.status == BarkochbaStatus.IN_STORE && it.userId == authRepository.userId
    }

    private fun GameResult.getBarkochbaQuestionCount() = barkochbaQuestions.count {
        it.status == BarkochbaStatus.IN_STORE
    }

    private suspend fun GameResult.getOwner(id: String) = if (id == authRepository.userId) {
        getString(resource = Res.string.own_name_label)
    } else {
        players.first { it.id == id }.name
    }

    private suspend fun GameResult.getHostName(isHost: Boolean) = if (isHost) {
        getString(resource = Res.string.own_name_label)
    } else {
        host.name
    }

    private suspend fun GameResult.mapQuestions(isHost: Boolean, hostName: String) =
        questions.immutableMap { question ->
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

                question.status == QuestionStatus.WAITING_FOR_PLAYERS && (isHost || question.userId == authRepository.userId) -> {
                    QuestionItem.WaitingForPlayersItem(
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName(),
                        hostName = hostName,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                    )
                }

                question.status == QuestionStatus.WAITING_FOR_PLAYERS -> {
                    QuestionItem.GuessAsPlayerItem(
                        id = question.id,
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName(),
                        hostName = hostName,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                    )
                }

                question.status == QuestionStatus.PLAYERS_WON -> {
                    QuestionItem.PlayersWonItem(
                        text = question.text,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                        answer = "${question.expectedFirstName} ${question.expectedLastName}",
                    )
                }

                question.status == QuestionStatus.GUESSED_BY_HOST -> {
                    QuestionItem.GuessedByHostItem(
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName().orEmpty(),
                        hostName = hostName,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                    )
                }

                question.status == QuestionStatus.WAITING_FOR_OWNER && question.userId == authRepository.userId -> {
                    QuestionItem.VerifyAsOwnerItem(
                        id = question.id,
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName().orEmpty(),
                        hostName = hostName,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                    )
                }

                question.status == QuestionStatus.WAITING_FOR_OWNER -> {
                    QuestionItem.WaitingForOwnerItem(
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName().orEmpty(),
                        hostName = hostName,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                    )
                }

                question.status == QuestionStatus.GUESSED_BY_PLAYER -> {
                    QuestionItem.GuessedByPlayerItem(
                        text = question.text,
                        answer = question.playerAnswer?.getName().orEmpty(),
                        answerOwner = question.playerAnswer?.userId?.let { id ->
                            getOwner(id = id)
                        }.orEmpty(),
                        hostAnswer = question.hostAnswer?.getName(),
                        hostName = hostName,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                    )
                }

                else -> {
                    QuestionItem.MissedByPlayerItem(
                        text = question.text,
                        answer = question.playerAnswer?.getName().orEmpty(),
                        answerOwner = question.playerAnswer?.userId?.let { id ->
                            getOwner(id = id)
                        }.orEmpty(),
                        hostAnswer = question.hostAnswer?.getName(),
                        hostName = hostName,
                        number = question.number,
                        owner = getOwner(id = question.userId),
                        expectedAnswer = "${question.expectedFirstName} ${question.expectedLastName ?: ""}"
                    )
                }
            }
        }

    private suspend fun GameResult.mapBarkochbaQuestions(isHost: Boolean, hostName: String) =
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
                        hostName = hostName,
                    )
                }

                else -> null
            }
        }
}
