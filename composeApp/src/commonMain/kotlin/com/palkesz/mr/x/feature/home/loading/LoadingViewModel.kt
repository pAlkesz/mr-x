package com.palkesz.mr.x.feature.home.loading

import androidx.lifecycle.ViewModel
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface LoadingViewModel {
	val viewState: StateFlow<LoadingViewState>

	fun onEventHandled()
}

class LoadingViewModelImpl(
	private val authRepository: AuthRepository,
	private val gameRepository: GameRepository,
	private val questionRepository: QuestionRepository
) : LoadingViewModel, ViewModel() {

	private val _viewState = MutableStateFlow(LoadingViewState())
	override val viewState = _viewState.asStateFlow()

	init {
		if (authRepository.isAuthenticated) {
			_viewState.update {
				it.copy(event = LoadingEvent.LoginSuccess)
			}
		}
		else {
			_viewState.update {
				it.copy(event = LoadingEvent.LoginFailure)
			}
		}
	}

	override fun onEventHandled() {
		_viewState.update {
			it.copy(event = null)
		}
	}
}
