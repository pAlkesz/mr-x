package com.palkesz.mr.x.feature.home.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.input.PrimaryTextField
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import com.palkesz.mr.x.feature.games.GameGraph
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.create_game_button_label
import mrx.composeapp.generated.resources.create_game_title
import mrx.composeapp.generated.resources.first_name_error_message
import mrx.composeapp.generated.resources.first_name_input_label
import mrx.composeapp.generated.resources.last_name_error_message
import mrx.composeapp.generated.resources.last_name_input_label
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateGameScreen(viewModel: CreateGameViewModel = koinViewModel<CreateGameViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    CreateGameScreenContent(
        viewState = viewState,
        onEventHandled = viewModel::onEventHandled,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onLastNameChanged = viewModel::onLastNameChanged,
        onCreateClicked = viewModel::onCreateClicked,
    )
}

@Composable
private fun CreateGameScreenContent(
    viewState: ViewState<CreateGameViewState>,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onCreateClicked: () -> Unit,
    onEventHandled: () -> Unit,
) {
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.create_game_title)
        )
    )
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onCreateClicked) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryTextField(
                modifier = Modifier.padding(bottom = 16.dp),
                value = state.firstName,
                onValueChanged = onFirstNameChanged,
                isValueValid = state.isFirstNameValid,
                label = stringResource(Res.string.first_name_input_label),
                error = stringResource(Res.string.first_name_error_message),
                imeAction = ImeAction.Next,
                showKeyboard = true,
            )
            PrimaryTextField(
                value = state.lastName,
                onValueChanged = onLastNameChanged,
                isValueValid = state.isLastNameValid,
                label = stringResource(Res.string.last_name_input_label),
                error = stringResource(Res.string.last_name_error_message),
                imeAction = ImeAction.Send,
                showKeyboard = false,
                onSend = { onCreateClicked() },
            )
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                onClick = onCreateClicked,
                enabled = state.isCreateButtonEnabled,
                text = stringResource(Res.string.create_game_button_label),
            )
        }
    }
}

@Composable
private fun HandleEvent(event: CreateGameEvent?, onEventHandled: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    HandleEventEffect(event) { createGameEvent, _, _, navController ->
        when (createGameEvent) {
            is CreateGameEvent.NavigateToGames -> {
                keyboardController?.hide()
                navController?.navigate(GameGraph.Games(joinedGameId = createGameEvent.gameId))
            }
        }
        onEventHandled()
    }
}
