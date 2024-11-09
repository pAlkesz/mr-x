package com.palkesz.mr.x.feature.authentication.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.helpers.showSnackbar
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.authentication.AuthGraph
import com.palkesz.mr.x.feature.authentication.ui.AuthInputForm
import com.palkesz.mr.x.feature.authentication.ui.ColumnWithMrxIcon
import com.palkesz.mr.x.feature.home.HomeGraph
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.email_field_label
import mrx.composeapp.generated.resources.invalid_email_label
import mrx.composeapp.generated.resources.login_link_sent_subtitle
import mrx.composeapp.generated.resources.login_link_sent_title
import mrx.composeapp.generated.resources.login_success_message
import mrx.composeapp.generated.resources.login_title
import mrx.composeapp.generated.resources.sending_login_link_button_label
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel<LoginViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    LoginScreenContent(
        viewState = viewState,
        onEmailChanged = viewModel::onEmailChanged,
        onSendLinkClicked = viewModel::onSendLinkClicked,
        onEventHandled = viewModel::onEventHandled
    )
}

@Composable
private fun LoginScreenContent(
    viewState: ViewState<LoginViewState>,
    onEmailChanged: (String) -> Unit,
    onSendLinkClicked: () -> Unit,
    onEventHandled: () -> Unit,
) {
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onSendLinkClicked) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        CrossFade(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            condition = state.isLinkSent,
            onConditionTrue = {
                EmailLinkSentForm(modifier = Modifier.fillMaxSize(), email = state.email)
            },
            onConditionFalse = {
                AuthInputForm(
                    modifier = Modifier.fillMaxSize(),
                    value = state.email,
                    isValueValid = state.isEmailValid,
                    title = stringResource(Res.string.login_title),
                    errorMessage = stringResource(Res.string.invalid_email_label),
                    inputLabel = stringResource(Res.string.email_field_label),
                    buttonText = stringResource(Res.string.sending_login_link_button_label),
                    isButtonEnabled = state.isSendButtonEnabled,
                    onValueChanged = onEmailChanged,
                    onButtonClicked = onSendLinkClicked,
                )
            })
    }
}

@Composable
private fun EmailLinkSentForm(modifier: Modifier = Modifier, email: String) {
    ColumnWithMrxIcon(modifier = modifier) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = stringResource(Res.string.login_link_sent_title, email),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.login_link_sent_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HandleEvent(event: LoginEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(key1 = event) { loginEvent, appScope, snackbarHostState, navController ->
        when (loginEvent) {
            is LoginEvent.NavigateToHome -> {
                appScope?.showSnackbar(
                    snackbarHostState = snackbarHostState,
                    message = getString(Res.string.login_success_message),
                )
                navController?.navigate(HomeGraph.Home)
            }

            is LoginEvent.NavigateToAddUsername -> {
                navController?.navigate(AuthGraph.AddUsername)
            }
        }
        onEventHandled()
    }
}
