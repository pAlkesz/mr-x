package com.palkesz.mr.x.feature.games.game

import androidx.compose.runtime.Immutable
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.question.BarkochbaQuestion
import com.palkesz.mr.x.core.model.question.Question
import com.palkesz.mr.x.core.model.user.User
import kotlinx.collections.immutable.ImmutableList

data class GameResult(
    val game: Game,
    val host: User,
    val questions: List<Question>,
    val barkochbaQuestions: List<BarkochbaQuestion>,
    val players: List<User>,
)

@Immutable
data class GameViewState(
    val host: String,
    val firstName: String,
    val lastName: String?,
    val isHost: Boolean,
    val isGameOngoing: Boolean,
    val isAskQuestionButtonVisible: Boolean,
    val isAskBarkochbaQuestionButtonVisible: Boolean,
    val barkochbaQuestionCount: Int,
    val questions: ImmutableList<QuestionItem>,
    val barkochbaQuestions: ImmutableList<BarkochbaItem>,
    val event: GameEvent? = null,
)

@Immutable
sealed interface QuestionItem {

    data class WaitingForHostItem(
        val text: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class GuessAsHostItem(
        val id: String,
        val text: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class WaitingForPlayersItem(
        val text: String,
        val hostAnswer: String?,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class GuessAsPlayerItem(
        val id: String,
        val text: String,
        val hostAnswer: String?,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class WaitingForOwnerItem(
        val text: String,
        val hostAnswer: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class VerifyAsOwnerItem(
        val id: String,
        val text: String,
        val hostAnswer: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class GuessedByPlayerItem(
        val text: String,
        val answer: String,
        val hostAnswer: String?,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class MissedByPlayerItem(
        val text: String,
        val answer: String,
        val hostAnswer: String?,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class GuessedByHostItem(
        val text: String,
        val hostAnswer: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class PlayersWonItem(
        val text: String,
        val number: Int,
        val owner: String,
        val answer: String,
    ) : QuestionItem

}

@Immutable
sealed interface BarkochbaItem {

    data class WaitingForHostItem(
        val text: String,
        val number: Int,
        val owner: String,
    ) : BarkochbaItem

    data class AnswerAsHostItem(
        val id: String,
        val text: String,
        val number: Int,
        val owner: String,
    ) : BarkochbaItem

    data class AnsweredByHostItem(
        val text: String,
        val number: Int,
        val owner: String,
        val answer: Boolean,
    ) : BarkochbaItem

}

@Immutable
sealed interface GameEvent {

    data class NavigateToCreateQuestion(val gameId: String) : GameEvent

    data class NavigateToCreateBarkochbaQuestion(val gameId: String) : GameEvent

    data class NavigateToSpecifyQuestion(val gameId: String, val questionId: String) : GameEvent

    data class NavigateToQrCode(val gameId: String) : GameEvent

    data class NavigateToGuessQuestion(val gameId: String, val questionId: String) : GameEvent

}
