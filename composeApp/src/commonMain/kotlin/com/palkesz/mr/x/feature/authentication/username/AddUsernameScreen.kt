package com.palkesz.mr.x.feature.authentication.username

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.ui.helpers.showSnackbar
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.authentication.ui.AuthInputForm
import com.palkesz.mr.x.feature.home.HomeGraph
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.add_username_title
import mrx.composeapp.generated.resources.invalid_username_label
import mrx.composeapp.generated.resources.login_success_message
import mrx.composeapp.generated.resources.save_button_label
import mrx.composeapp.generated.resources.username_field_label
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddUsernameScreen(viewModel: AddUsernameViewModel = koinViewModel<AddUsernameViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    AddUsernameScreenContent(
        viewState = viewState,
        onUsernameChanged = viewModel::onUsernameChanged,
        onSaveClicked = viewModel::onSaveClicked,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun AddUsernameScreenContent(
    viewState: ViewState<AddUserNameViewState>,
    onUsernameChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onEventHandled: () -> Unit,
) {
    TitleBarEffect(details = null)
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onSaveClicked) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        AuthInputForm(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            value = state.username,
            isValueValid = state.isUserNameValid,
            title = stringResource(Res.string.add_username_title),
            errorMessage = stringResource(Res.string.invalid_username_label),
            buttonText = stringResource(Res.string.save_button_label),
            isButtonEnabled = state.isSaveButtonEnabled,
            inputLabel = stringResource(Res.string.username_field_label),
            keyboardType = KeyboardType.Text,
            onValueChanged = onUsernameChanged,
            onButtonClicked = onSaveClicked,
        )
    }
}

@Composable
private fun HandleEvent(
    event: AddUserNameEvent?,
    onEventHandled: () -> Unit,
) {
    HandleEventEffect(key1 = event) { addUserNameEvent, appScope, snackbarHostState, navController ->
        when (addUserNameEvent) {
            is AddUserNameEvent.NavigateToHome -> {
                appScope?.showSnackbar(
                    snackbarHostState = snackbarHostState,
                    message = getString(Res.string.login_success_message),
                )
                navController?.navigate(HomeGraph.Home)
            }
        }
        onEventHandled()
    }
}
