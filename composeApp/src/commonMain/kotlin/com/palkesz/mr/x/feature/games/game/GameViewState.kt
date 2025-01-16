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
    val host: String?,
    val firstName: String,
    val lastName: String?,
    val isHost: Boolean,
    val isGameOngoing: Boolean,
    val isAskQuestionButtonVisible: Boolean,
    val isAskBarkochbaQuestionButtonVisible: Boolean,
    val barkochbaQuestionCount: Int,
    val questions: ImmutableList<QuestionItem>,
    val barkochbaQuestions: ImmutableList<BarkochbaItem>,
    val questionBadgeCount: Int?,
    val barkochbaBadgeCount: Int?,
    val animatedQuestionId: String?,
    val animatedBarkochbaQuestionId: String?,
    val event: GameEvent? = null,
)

@Immutable
sealed interface QuestionItem {

    val id: String

    data class WaitingForHostItem(
        override val id: String,
        val text: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class GuessAsHostItem(
        override val id: String,
        val text: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class WaitingForPlayersItem(
        override val id: String,
        val text: String,
        val hostAnswer: String?,
        val hostName: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class GuessAsPlayerItem(
        override val id: String,
        val text: String,
        val hostAnswer: String?,
        val hostName: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class WaitingForOwnerItem(
        override val id: String,
        val text: String,
        val hostAnswer: String,
        val hostName: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class VerifyAsOwnerItem(
        override val id: String,
        val text: String,
        val hostAnswer: String,
        val hostName: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class GuessedByPlayerItem(
        override val id: String,
        val text: String,
        val answer: String,
        val answerOwner: String,
        val hostAnswer: String?,
        val hostName: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class MissedByPlayerItem(
        override val id: String,
        val text: String,
        val answer: String,
        val answerOwner: String,
        val hostAnswer: String?,
        val hostName: String,
        val number: Int,
        val owner: String,
        val expectedAnswer: String,
    ) : QuestionItem

    data class GuessedByHostItem(
        override val id: String,
        val text: String,
        val hostAnswer: String,
        val hostName: String,
        val number: Int,
        val owner: String,
    ) : QuestionItem

    data class PlayersWonItem(
        override val id: String,
        val text: String,
        val number: Int,
        val owner: String,
        val answer: String,
    ) : QuestionItem

}

@Immutable
sealed interface BarkochbaItem {

    val id: String

    data class WaitingForHostItem(
        override val id: String,
        val text: String,
        val number: Int,
        val owner: String,
    ) : BarkochbaItem

    data class AnswerAsHostItem(
        override val id: String,
        val text: String,
        val number: Int,
        val owner: String,
    ) : BarkochbaItem

    data class AnsweredByHostItem(
        override val id: String,
        val text: String,
        val number: Int,
        val owner: String,
        val answer: Boolean,
        val hostName: String,
    ) : BarkochbaItem

}

@Immutable
sealed interface GameEvent {

    data class GoToTab(val index: Int) : GameEvent

    data class NavigateToCreateQuestion(val gameId: String) : GameEvent

    data class NavigateToCreateBarkochbaQuestion(val gameId: String) : GameEvent

    data class NavigateToSpecifyQuestion(val gameId: String, val questionId: String) : GameEvent

    data class NavigateToQrCode(val gameId: String) : GameEvent

    data class NavigateToGuessQuestion(val gameId: String, val questionId: String) : GameEvent

}
