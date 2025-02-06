package com.palkesz.mr.x.feature.authentication.login

import com.palkesz.mr.x.BaseTest
import com.palkesz.mr.x.KonnectivityStub
import com.palkesz.mr.x.core.usecase.auth.SendSignInLinkUseCase
import com.palkesz.mr.x.core.util.networking.ViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginViewModelTest : BaseTest() {

    @Test
    fun `Default view state upon view model creation`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            sendSignInLinkResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        assertEquals(
            expected = ViewState.Success(
                LoginViewState(
                    email = "",
                    isEmailValid = true,
                    isLinkSent = false,
                    isSendButtonEnabled = true,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Send link button disable when device is offline`() = runTest {
        val isConnectedState = flowOf(value = false).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = false
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            sendSignInLinkResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        assertEquals(
            expected = ViewState.Success(
                LoginViewState(
                    email = "",
                    isEmailValid = true,
                    isLinkSent = false,
                    isSendButtonEnabled = false,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Updating email text`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            sendSignInLinkResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onEmailChanged(email = TEST_EMAIL)
        assertEquals(
            expected = ViewState.Success(
                LoginViewState(
                    email = TEST_EMAIL,
                    isEmailValid = true,
                    isLinkSent = false,
                    isSendButtonEnabled = true,
                )
            ),
            actual = viewModel.viewState.value,
        )
    }

    @Test
    fun `Sending link with validation error`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            sendSignInLinkResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onSendLinkClicked()
        assertEquals(
            expected = ViewState.Success(
                LoginViewState(
                    email = "",
                    isEmailValid = false,
                    isLinkSent = false,
                    isSendButtonEnabled = false,
                )
            ),
            actual = viewModel.viewState.value,
        )
    }

    @Test
    fun `Sending link successfully`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            sendSignInLinkResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onEmailChanged(email = TEST_EMAIL)
        viewModel.onSendLinkClicked()
        assertEquals(
            expected = ViewState.Success(
                LoginViewState(
                    email = TEST_EMAIL,
                    isEmailValid = true,
                    isLinkSent = true,
                    isSendButtonEnabled = true,
                )
            ),
            actual = viewModel.viewState.value,
        )
    }

    @Test
    fun `Sending link with error`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            sendSignInLinkResult = Result.failure(exception = Exception()),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onEmailChanged(email = TEST_EMAIL)
        viewModel.onSendLinkClicked()
        assertEquals(
            expected = ViewState.Failure(isLoading = false),
            actual = viewModel.viewState.value,
        )
    }

    private fun getViewModel(
        sendSignInLinkResult: Result<Unit>,
        konnectivity: KonnectivityStub,
    ): LoginViewModel {
        val sendSignInLinkUseCase = SendSignInLinkUseCase { sendSignInLinkResult }
        return LoginViewModelImpl(
            sendSignInLinkUseCase = sendSignInLinkUseCase,
            konnectivity = konnectivity,
        )
    }

    companion object {
        private const val TEST_EMAIL = "test@gmail.com"
    }
}
