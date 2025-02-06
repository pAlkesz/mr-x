package com.palkesz.mr.x.feature.authentication.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.AUTH_TAG
import com.palkesz.mr.x.core.usecase.auth.SendSignInLinkUseCase
import com.palkesz.mr.x.core.util.extensions.validateAsEmail
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import com.plusmobileapps.konnectivity.Konnectivity
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface LoginViewModel {
    val viewState: StateFlow<ViewState<LoginViewState>>

    fun onEmailChanged(email: String)
    fun onSendLinkClicked()
}

@Stable
class LoginViewModelImpl(
    private val sendSignInLinkUseCase: SendSignInLinkUseCase,
    konnectivity: Konnectivity,
) : ViewModel(), LoginViewModel {

    private val dataLoader = DataLoader<Unit>()

    private val refreshTrigger = RefreshTrigger()

    private val email = MutableStateFlow(value = "")

    private val isEmailValid = MutableStateFlow(value = true)

    private val isLinkSent = MutableStateFlow(value = false)

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Success(Unit),
        fetchData = { sendSignInLink() },
    )

    override val viewState =
        combine(
            loadingResult,
            email,
            isEmailValid,
            isLinkSent,
            konnectivity.isConnectedState
        ) { result, email, isEmailValid, isLinkSent, isConnected ->
            result.map {
                LoginViewState(
                    email = email,
                    isEmailValid = isEmailValid,
                    isLinkSent = isLinkSent,
                    isSendButtonEnabled = isConnected && isEmailValid,
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ViewState.Success(
                LoginViewState(
                    email = email.value,
                    isEmailValid = isEmailValid.value,
                    isLinkSent = isLinkSent.value,
                    isSendButtonEnabled = konnectivity.isConnected,
                )
            )
        )

    override fun onEmailChanged(email: String) {
        this.email.update { email }
        isEmailValid.update { true }
    }

    override fun onSendLinkClicked() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    private suspend fun sendSignInLink() = if (!email.value.validateAsEmail()) {
        isEmailValid.update { false }
        Result.success(Unit)
    } else {
        isLinkSent.update { true }
        sendSignInLinkUseCase.run(email = email.value).also {
            Napier.d(tag = AUTH_TAG) { "Sending sign in link: $it" }
        }
    }
}
