package com.palkesz.mr.x.feature.authentication.username

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.AppStateEffect
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.app.ShowSnackbar
import com.palkesz.mr.x.feature.authentication.ui.AuthInputForm
import com.palkesz.mr.x.feature.home.HomeGraphRoute
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.add_username_title
import mrx.composeapp.generated.resources.invalid_username_label
import mrx.composeapp.generated.resources.login_success_message
import mrx.composeapp.generated.resources.save_button_label
import mrx.composeapp.generated.resources.username_field_label
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
fun AddUsernameScreenContent(
    viewState: ViewState<AddUserNameViewState>,
    onUsernameChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onEventHandled: () -> Unit,
) {
    AppStateEffect { appState ->
        appState.hideAppBars()
    }
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onSaveClicked) { state ->
        HandleEvent(
            event = state.event,
            onEventHandled = onEventHandled,
        )
        AuthInputForm(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            value = state.username,
            isValueValid = state.isUserNameValid,
            title = stringResource(Res.string.add_username_title),
            errorMessage = stringResource(Res.string.invalid_username_label),
            buttonText = stringResource(Res.string.save_button_label),
            inputLabel = stringResource(Res.string.username_field_label),
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
    event?.let {
        when (event) {
            is AddUserNameEvent.NavigateToHome -> {
                ShowSnackbar(message = stringResource(Res.string.login_success_message))
                LocalNavController.current?.navigate(HomeGraphRoute.HomePage.route)
            }
        }
        onEventHandled()
    }
}
