package com.palkesz.mr.x.feature.games.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.BarkochbaStatus
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.util.extensions.capitalizeWords
import com.palkesz.mr.x.core.util.extensions.getName
import com.palkesz.mr.x.core.util.extensions.immutableMapNotNull
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.own_name_label
import org.jetbrains.compose.resources.getString

fun interface GameUiMapper {
    suspend fun mapViewState(
        result: GameResult,
        notificationCount: Pair<Int, Int>,
        addedQuestionId: String?,
        addedBarkochbaQuestionId: String?,
        event: GameEvent?
    ): GameViewState
}

class GameUiMapperImpl(
    private val authRepository: AuthRepository,
) : GameUiMapper {

    override suspend fun mapViewState(
        result: GameResult,
        notificationCount: Pair<Int, Int>,
        addedQuestionId: String?,
        addedBarkochbaQuestionId: String?,
        event: GameEvent?
    ): GameViewState {
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
            questionBadgeCount = notificationCount.first.takeIf { it > 0 },
            barkochbaBadgeCount = notificationCount.second.takeIf { it > 0 },
            animatedQuestionId = addedQuestionId,
            animatedBarkochbaQuestionId = addedBarkochbaQuestionId,
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
        players.firstOrNull { it.id == id }?.name
    }

    private suspend fun GameResult.getHostName(isHost: Boolean) = if (isHost) {
        getString(resource = Res.string.own_name_label)
    } else {
        host.name
    }

    private suspend fun GameResult.mapQuestions(isHost: Boolean, hostName: String) =
        questions.immutableMapNotNull { question ->
            val owner = getOwner(id = question.userId) ?: return@immutableMapNotNull null
            val answerOwner = question.playerAnswer?.userId?.let { id ->
                getOwner(id = id)
            }
            when {
                question.status == QuestionStatus.WAITING_FOR_HOST && isHost -> {
                    QuestionItem.GuessAsHostItem(
                        id = question.id,
                        text = question.text,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == QuestionStatus.WAITING_FOR_HOST -> {
                    QuestionItem.WaitingForHostItem(
                        id = question.id,
                        text = question.text,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == QuestionStatus.WAITING_FOR_PLAYERS && (isHost || question.userId == authRepository.userId) -> {
                    QuestionItem.WaitingForPlayersItem(
                        id = question.id,
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName(),
                        hostName = hostName,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == QuestionStatus.WAITING_FOR_PLAYERS -> {
                    QuestionItem.GuessAsPlayerItem(
                        id = question.id,
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName(),
                        hostName = hostName,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == QuestionStatus.PLAYERS_WON -> {
                    QuestionItem.PlayersWonItem(
                        id = question.id,
                        text = question.text,
                        number = question.number,
                        owner = owner,
                        answer = "${question.expectedFirstName} ${question.expectedLastName.orEmpty()}"
                            .trim()
                            .capitalizeWords(),
                    )
                }

                question.status == QuestionStatus.GUESSED_BY_HOST -> {
                    QuestionItem.GuessedByHostItem(
                        id = question.id,
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName().orEmpty(),
                        hostName = hostName,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == QuestionStatus.WAITING_FOR_OWNER && question.userId == authRepository.userId -> {
                    QuestionItem.VerifyAsOwnerItem(
                        id = question.id,
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName().orEmpty(),
                        hostName = hostName,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == QuestionStatus.WAITING_FOR_OWNER -> {
                    QuestionItem.WaitingForOwnerItem(
                        id = question.id,
                        text = question.text,
                        hostAnswer = question.hostAnswer?.getName().orEmpty(),
                        hostName = hostName,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == QuestionStatus.GUESSED_BY_PLAYER -> {
                    answerOwner?.let {
                        QuestionItem.GuessedByPlayerItem(
                            id = question.id,
                            text = question.text,
                            answer = question.playerAnswer.getName(),
                            answerOwner = answerOwner,
                            hostAnswer = question.hostAnswer?.getName(),
                            hostName = hostName,
                            number = question.number,
                            owner = owner,
                        )
                    }
                }

                else -> {
                    answerOwner?.let {
                        QuestionItem.MissedByPlayerItem(
                            id = question.id,
                            text = question.text,
                            answer = question.playerAnswer.getName(),
                            answerOwner = answerOwner,
                            hostAnswer = question.hostAnswer?.getName(),
                            hostName = hostName,
                            number = question.number,
                            owner = owner,
                            expectedAnswer = "${question.expectedFirstName} ${question.expectedLastName ?: ""}"
                        )
                    }
                }
            }
        }

    private suspend fun GameResult.mapBarkochbaQuestions(
        isHost: Boolean,
        hostName: String
    ) =
        barkochbaQuestions.immutableMapNotNull { question ->
            val owner = getOwner(id = question.userId) ?: return@immutableMapNotNull null
            when {
                question.status == BarkochbaStatus.ASKED && isHost -> {
                    BarkochbaItem.AnswerAsHostItem(
                        id = question.id,
                        text = question.text,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == BarkochbaStatus.ASKED -> {
                    BarkochbaItem.WaitingForHostItem(
                        id = question.id,
                        text = question.text,
                        number = question.number,
                        owner = owner,
                    )
                }

                question.status == BarkochbaStatus.ANSWERED -> {
                    BarkochbaItem.AnsweredByHostItem(
                        id = question.id,
                        text = question.text,
                        number = question.number,
                        owner = owner,
                        answer = question.answer,
                        hostName = hostName,
                    )
                }

                else -> null
            }
        }
}
