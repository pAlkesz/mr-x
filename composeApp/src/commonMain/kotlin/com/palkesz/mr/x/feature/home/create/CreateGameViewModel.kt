package com.palkesz.mr.x.feature.home.create

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.usecase.game.CreateGameUseCase
import com.palkesz.mr.x.core.util.extensions.combine
import com.palkesz.mr.x.core.util.extensions.validateAsName
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface CreateGameViewModel {
    val viewState: StateFlow<ViewState<CreateGameViewState>>

    fun onFirstNameChanged(firstName: String)
    fun onLastNameChanged(lastName: String)
    fun onCreateClicked()
    fun onEventHandled()
}

@Stable
class CreateGameViewModelImpl(
    private val createGameUseCase: CreateGameUseCase,
    konnectivity: Konnectivity,
) : ViewModel(), CreateGameViewModel {

    private val dataLoader = DataLoader<Unit>()

    private val refreshTrigger = RefreshTrigger()

    private val firstName = MutableStateFlow("")

    private val lastName = MutableStateFlow("")

    private val isFirstNameValid = MutableStateFlow(true)

    private val isLastNameValid = MutableStateFlow(true)

    private val event = MutableStateFlow<CreateGameEvent?>(null)

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Success(Unit),
        fetchData = { createGame() })

    override val viewState = combine(
        loadingResult,
        firstName,
        lastName,
        isFirstNameValid,
        isLastNameValid,
        konnectivity.isConnectedState,
        event
    ) { result, firstName, lastName, isFirstNameValid, isLastNameValid, isConnected, event ->
        result.map {
            CreateGameViewState(
                firstName = firstName,
                lastName = lastName,
                isFirstNameValid = isFirstNameValid,
                isLastNameValid = isLastNameValid,
                isCreateButtonEnabled = isConnected && isFirstNameValid && isLastNameValid,
                event = event,
            )
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), ViewState.Success(
            CreateGameViewState(
                firstName = firstName.value,
                lastName = lastName.value,
                isFirstNameValid = isFirstNameValid.value,
                isLastNameValid = isLastNameValid.value,
                isCreateButtonEnabled = konnectivity.isConnected,
                event = event.value,
            )
        )
    )

    override fun onFirstNameChanged(firstName: String) {
        this.firstName.update { firstName }
        isFirstNameValid.update { true }
    }

    override fun onLastNameChanged(lastName: String) {
        this.lastName.update { lastName }
        isLastNameValid.update { true }
    }

    override fun onCreateClicked() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

    private suspend fun createGame(): Result<Unit> {
        val isFirstNameValid = firstName.value.isNotBlank() && firstName.value.validateAsName()
        val isLastNameValid = lastName.value.validateAsName()
        this.isFirstNameValid.update { isFirstNameValid }
        this.isLastNameValid.update { isLastNameValid }
        return if (isFirstNameValid && isLastNameValid) {
            createGameUseCase.run(
                firstName = firstName.value.trim(),
                lastName = lastName.value.trim().takeIf { it.isNotBlank() }).onSuccess { game ->
                event.update { CreateGameEvent.NavigateToGames(gameId = game.id) }
            }.map { Unit }
        } else {
            Result.success(Unit)
        }
    }
}
