package com.palkesz.mr.x.feature.games.gameDetailsScreen

import androidx.compose.runtime.Immutable
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.ui.components.ContainsText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import mrx.mrx_app.generated.resources.*
import org.jetbrains.compose.resources.StringResource

@Immutable
data class GameDetailsViewState(
	val hostName: String = "",
	val defaultTitle: String = "",
	val optionalHiddenTitle: String? = null,
	val playerIsHost: Boolean = true,
	val isAskQuestionButtonVisible: Boolean,
	val gameStatus: GameStatus,
	val event: GameDetailsEvent? = null,
	val questions: ImmutableList<QuestionItem> = persistentListOf(),
	val selectedFilters: ImmutableList<QuestionItemFilter> = persistentListOf()
)

sealed interface GameDetailsEvent {
	data class AskQuestionClicked(val gameId: String) : GameDetailsEvent
	data class NavigateToAnswerScreen(
		val questionId: String,
		val gameId: String,
		val isHost: Boolean
	) : GameDetailsEvent

	data class NavigateToSpecifyQuestionScreen(
		val questionId: String,
		val gameId: String
	) : GameDetailsEvent
}

@Immutable
sealed interface QuestionItem {
	val text: String
	val uuid: String
}

@Immutable
data class HostAnswersQuestionItem(
	override val text: String,
	override val uuid: String,
	val gameId: String
) : QuestionItem

@Immutable
data class HostWaitsForPlayersQuestionItem(
	override val text: String,
	override val uuid: String,
) : QuestionItem

@Immutable
data class HostWaitsForOwnerQuestionItem(
	override val text: String,
	override val uuid: String,
	val hostAnswer: String? = null,
) : QuestionItem

@Immutable
data class CorrectAnswerQuestionItem(
	override val text: String,
	override val uuid: String,
	val isHost: Boolean,
	val hostAnswer: String? = null,
	val correctAnswer: String? = null
) : QuestionItem

@Immutable
data class WrongAnswerQuestionItem(
	override val text: String,
	override val uuid: String,
	val hostAnswer: String? = null,
	val playerAnswer: String? = null,
	val expectedAnswer: String? = null
) : QuestionItem

@Immutable
data class HostSeesBarkochbaQuestionItem(
	override val text: String,
	override val uuid: String,
	val playerAnswer: String? = null,
	val barkochbaText: String,
) : QuestionItem

@Immutable
data class BarkochbaAnsweredQuestionItem(
	override val text: String,
	override val uuid: String,
	val hostAnswer: String? = null,
	val correctAnswer: String? = null,
	val barkochbaText: String,
	val barkochbaAnswer: Boolean
) : QuestionItem

@Immutable
data class PlayerWaitsForHostQuestionItem(
	override val text: String,
	override val uuid: String,
) : QuestionItem

@Immutable
data class PlayerCanAnswerQuestionItem(
	override val text: String,
	override val uuid: String,
	val gameId: String
) : QuestionItem

@Immutable
data class PlayerWaitsForOwnerQuestionItem(
	override val text: String,
	override val uuid: String,
	val hostAnswer: String? = null
) : QuestionItem

@Immutable
data class OwnerEvaluatesQuestionItem(
	override val text: String,
	override val uuid: String,
	val gameId: String,
	val hostAnswer: String? = null
) : QuestionItem

@Immutable
data class QuestionOwnerWaitsForPlayers(
	override val text: String,
	override val uuid: String
) : QuestionItem

@Immutable
data class PlayerSeesBarkochbaQuestionItem(
	override val text: String,
	override val uuid: String,
	val hostAnswer: String? = null,
	val playerAnswer: String? = null,
	val barkochbaText: String
) : QuestionItem

@Immutable
data class GuessedByHostQuestionItem(
	override val text: String,
	override val uuid: String,
	val isHost: Boolean,
	val hostAnswer: String? = null
) : QuestionItem

@Immutable
data class PlayerWonQuestionItem(
	override val text: String,
	override val uuid: String,
	val answer: String
) : QuestionItem

enum class QuestionItemFilter(override val text: StringResource) : ContainsText {
	WON(Res.string.question_filter_text_won),
	PLAYER_TURN(Res.string.question_filter_text_player_turn),
	WAITING_FOR_OTHERS(Res.string.question_filter_text_waiting),
	BARKOCHBA(Res.string.question_filter_text_barkochba),
	CORRECT_ANSWER(Res.string.question_filter_text_correct),
	WRONG_ANSWER(Res.string.question_filter_text_wrong),
	GUESSED_BY_HOST(Res.string.question_filter_text_guessed_by_host)
}
