package com.palkesz.mr.x.feature.games.gameDetailsScreen

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.usecase.game.AnswerBarkochbaQuestionUseCase
import com.palkesz.mr.x.core.usecase.game.GetGameWithHostUseCase
import com.palkesz.mr.x.core.usecase.game.GetQuestionsOfGameUseCase
import com.palkesz.mr.x.core.usecase.game.PassQuestionAsHostUseCase
import com.palkesz.mr.x.core.usecase.game.question.DeclineHostAnswerUseCase
import com.palkesz.mr.x.core.util.*
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
class GamesDetailViewModel(
	private val authRepository: AuthRepository,
	private val getGameWithHostUseCase: GetGameWithHostUseCase,
	private val getQuestionsOfGameUseCase: GetQuestionsOfGameUseCase,
	private val passQuestionAsHostUseCase: PassQuestionAsHostUseCase,
	private val gameDetailsUiMapper: GameDetailsUiMapper,
	private val declineHostAnswerUseCase: DeclineHostAnswerUseCase,
	private val answerBarkochbaQuestionUseCase: AnswerBarkochbaQuestionUseCase,
	private val questionRepository: QuestionRepository
) : ViewModel() {

	private var gameId: String? = null

	private val _viewState = MutableStateFlow<ViewState<GameDetailsViewState>>(ViewState.Loading)
	val viewState: StateFlow<ViewState<GameDetailsViewState>> = _viewState.asStateFlow()

	private fun getGame() {
		gameId?.let {
			viewModelScope.launch {
				getGameWithHostUseCase.run(it).combineResultPair(
					getQuestionsOfGameUseCase.run(it)
				).collect { pair ->
					_viewState.update {
						when (pair) {
							is Error -> ViewState.Failure
							is Loading -> ViewState.Loading
							is Success -> ViewState.Success(
								gameDetailsUiMapper.mapViewState(
									pair.result,
									authRepository.currentUserId,
									it.getOrNull()?.selectedFilters ?: persistentListOf()
								)
							)
						}
					}
				}
			}
		}
	}

	fun onFilterSelected(filter: QuestionItemFilter) {
		gameId?.let { gameId ->
			_viewState.updateIfSuccess { currentState ->
				val selectedFilters =
					if (currentState.selectedFilters.contains(filter)) {
						(currentState.selectedFilters - filter).toPersistentList()
					}
					else {
						(currentState.selectedFilters + filter).toPersistentList()
					}
				currentState.copy(
					selectedFilters = selectedFilters,
					questions = gameDetailsUiMapper.mapQuestions(
						questions = questionRepository.playerQuestions.value.filter {
							it.gameId == gameId
						},
						filters = selectedFilters,
						playerIsHost = currentState.playerIsHost,
						userId = authRepository.currentUserId
					)
				)
			}
		}
	}

	fun onAskQuestionClicked() {
		gameId?.let { gameId ->
			_viewState.updateIfSuccess { currentState ->
				currentState.copy(event = GameDetailsEvent.AskQuestionClicked(gameId))
			}
		}
	}

	fun onDeclineHostAnswerClicked(questionId: String) {
		declineHostAnswerUseCase.run(questionId)
	}

	fun onAcceptHostAnswerClicked(questionId: String, gameId: String) {
		_viewState.updateIfSuccess { currentState ->
			currentState.copy(
				event = GameDetailsEvent.NavigateToSpecifyQuestionScreen(questionId, gameId)
			)
		}
	}

	fun setGameId(gameId: String) {
		this.gameId = gameId
		getGame()
	}

	fun onEventHandled() {
		_viewState.updateIfSuccess { currentState ->
			currentState.copy(event = null)
		}
	}

	fun onPlayerAnswerClicked(questionId: String, gameId: String, isHost: Boolean) {
		_viewState.updateIfSuccess { currentState ->
			currentState.copy(
				event = GameDetailsEvent.NavigateToAnswerScreen(
					questionId,
					gameId,
					isHost
				)
			)
		}
	}

	fun onBarkochbaAnswered(answer: Boolean, questionId: String) {
		answerBarkochbaQuestionUseCase.run(answer, questionId)
	}

	fun onHostPassedClicked(questionId: String) {
		passQuestionAsHostUseCase.run(questionId)
	}

	fun onRetry() {
		getGame()
	}

}
