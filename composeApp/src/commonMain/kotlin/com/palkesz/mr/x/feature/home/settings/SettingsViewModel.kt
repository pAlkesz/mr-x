package com.palkesz.mr.x.feature.home.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.usecase.auth.DeleteAccountUseCase
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.updateIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface SettingsViewModel {
    val viewState: StateFlow<ViewState<SettingsViewState>>

    fun onDeleteAccountClicked()
    fun onDeleteAccountConfirmClicked()
    fun onRetry()
    fun onEventHandled()
}

@Stable
class SettingsViewModelImpl(
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel(), SettingsViewModel {

    private val _viewState =
        MutableStateFlow<ViewState<SettingsViewState>>(value = ViewState.Success(data = SettingsViewState()))
    override val viewState = _viewState.asStateFlow()

    override fun onDeleteAccountClicked() {
        _viewState.updateIfSuccess { it.copy(event = SettingsEvent.ShowDeleteAccountDialog) }
    }

    override fun onDeleteAccountConfirmClicked() {
        deleteAccount()
    }

    override fun onRetry() {
        deleteAccount()
    }

    override fun onEventHandled() {
        _viewState.updateIfSuccess { it.copy(event = null) }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _viewState.update { ViewState.Loading }
            deleteAccountUseCase.run().onSuccess {
                _viewState.update { ViewState.Success(data = SettingsViewState()) }
            }.onFailure {
                _viewState.update { ViewState.Failure() }
            }
        }
    }
}
