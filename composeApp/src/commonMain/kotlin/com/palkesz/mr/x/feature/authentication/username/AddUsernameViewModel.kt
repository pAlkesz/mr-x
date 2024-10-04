package com.palkesz.mr.x.feature.authentication.username

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.AUTH_TAG
import com.palkesz.mr.x.core.util.extensions.validateAsUsername
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface AddUsernameViewModel {
    val viewState: StateFlow<ViewState<AddUserNameViewState>>

    fun onUsernameChanged(username: String)
    fun onSaveClicked()
    fun onEventHandled()
}

@Stable
class AddUsernameViewModelImpl(
    private val authRepository: AuthRepository,
) : ViewModel(), AddUsernameViewModel {

    private val dataLoader = DataLoader<Unit>()

    private val refreshTrigger = RefreshTrigger()

    private val username = MutableStateFlow("")

    private val isUsernameValid = MutableStateFlow(true)

    private val event = MutableStateFlow<AddUserNameEvent?>(null)

    private val saveUsernameResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Success(Unit),
        fetchData = { saveUsername() })

    override val viewState =
        combine(
            saveUsernameResult,
            username,
            isUsernameValid,
            event
        ) { result, username, isUsernameValid, event ->
            result.map {
                AddUserNameViewState(
                    username = username,
                    isUserNameValid = isUsernameValid,
                    event = event,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ViewState.Loading)

    override fun onUsernameChanged(username: String) {
        this.username.update { username }
    }

    override fun onSaveClicked() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

    private suspend fun saveUsername() = if (!username.value.validateAsUsername()) {
        isUsernameValid.update { false }
        Result.success(Unit)
    } else {
        authRepository.updateUsername(name = username.value.trim()).also {
            Napier.d(tag = AUTH_TAG) { "Updating username: $it" }
        }.onSuccess {
            event.update { AddUserNameEvent.NavigateToHome }
        }
    }

}
