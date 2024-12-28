package com.palkesz.mr.x.feature.games.question.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.input.PrimaryTextField
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.ui.helpers.QuestionMarkTransformation
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import com.palkesz.mr.x.feature.games.GameGraph
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_question_button_label
import mrx.composeapp.generated.resources.create_question_screen_title
import mrx.composeapp.generated.resources.expected_last_name_input_label
import mrx.composeapp.generated.resources.first_name_input_label
import mrx.composeapp.generated.resources.question_first_name_error_message
import mrx.composeapp.generated.resources.question_input_error_message
import mrx.composeapp.generated.resources.question_input_label
import mrx.composeapp.generated.resources.question_last_name_error_message
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateQuestionScreen(viewModel: CreateQuestionViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    CreateQuestionScreenContent(
        viewState = viewState,
        onEventHandled = viewModel::onEventHandled,
        onTextChanged = viewModel::onTextChanged,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onLastNameChanged = viewModel::onLastNameChanged,
        onCreateClicked = viewModel::onCreateClicked,
    )
}

@Composable
private fun CreateQuestionScreenContent(
    viewState: ViewState<CreateQuestionViewState>,
    onTextChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onCreateClicked: () -> Unit,
    onEventHandled: () -> Unit,
) {
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.create_question_screen_title)
        )
    )
    ContentWithBackgroundLoadingIndicator(
        state = viewState,
        onRetry = onCreateClicked,
    ) { state ->
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
                value = state.text,
                onValueChanged = onTextChanged,
                isValueValid = state.isTextValid,
                label = stringResource(Res.string.question_input_label),
                error = stringResource(Res.string.question_input_error_message),
                visualTransformation = QuestionMarkTransformation,
                showKeyboard = true,
            )
            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                PrimaryTextField(
                    modifier = Modifier.padding(end = 16.dp).weight(1f),
                    value = state.firstName,
                    onValueChanged = onFirstNameChanged,
                    isValueValid = state.isFirstNameValid,
                    label = stringResource(Res.string.first_name_input_label),
                    error = stringResource(Res.string.question_first_name_error_message),
                    showKeyboard = false,
                )
                PrimaryTextField(
                    modifier = Modifier.weight(1f),
                    value = state.lastName,
                    onValueChanged = onLastNameChanged,
                    isValueValid = state.isLastNameValid,
                    label = stringResource(Res.string.expected_last_name_input_label),
                    error = stringResource(Res.string.question_last_name_error_message),
                    showKeyboard = false,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                onClick = onCreateClicked,
                enabled = state.isCreateButtonEnabled,
                text = stringResource(Res.string.ask_question_button_label),
            )
        }
    }
}

@Composable
private fun HandleEvent(event: CreateQuestionEvent?, onEventHandled: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    HandleEventEffect(event) { createQuestionEvent, _, _, navController ->
        when (createQuestionEvent) {
            is CreateQuestionEvent.NavigateUp -> {
                keyboardController?.hide()
                navController?.navigate(
                    route = GameGraph.Game(
                        id = createQuestionEvent.gameId,
                        addedQuestionId = createQuestionEvent.questionId,
                        addedBarkochbaQuestionId = null,
                    )
                )
            }
        }
        onEventHandled()
    }
}
