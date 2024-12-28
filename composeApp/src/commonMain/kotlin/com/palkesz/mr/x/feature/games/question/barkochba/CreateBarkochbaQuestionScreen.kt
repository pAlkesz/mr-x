package com.palkesz.mr.x.feature.games.question.barkochba

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
import mrx.composeapp.generated.resources.create_barkochba_question_screen_title
import mrx.composeapp.generated.resources.question_input_error_message
import mrx.composeapp.generated.resources.question_input_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateBarkochbaQuestionScreen(viewModel: CreateBarkochbaQuestionViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    CreateBarkochbaQuestionScreenContent(
        viewState = viewState,
        onTextChanged = viewModel::onTextChanged,
        onCreateClicked = viewModel::onCreateClicked,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun CreateBarkochbaQuestionScreenContent(
    viewState: ViewState<CreateBarkochbaQuestionViewState>,
    onTextChanged: (String) -> Unit,
    onCreateClicked: () -> Unit,
    onEventHandled: () -> Unit,
) {
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.create_barkochba_question_screen_title)
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
                modifier = Modifier.padding(vertical = 16.dp),
                value = state.text,
                onValueChanged = onTextChanged,
                isValueValid = state.isTextValid,
                label = stringResource(Res.string.question_input_label),
                error = stringResource(Res.string.question_input_error_message),
                visualTransformation = QuestionMarkTransformation,
                showKeyboard = true,
            )
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
private fun HandleEvent(event: CreateBarkochbaQuestionEvent?, onEventHandled: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    HandleEventEffect(event) { createEvent, _, _, navController ->
        when (createEvent) {
            is CreateBarkochbaQuestionEvent.NavigateUp -> {
                keyboardController?.hide()
                navController?.navigate(
                    route = GameGraph.Game(
                        id = createEvent.gameId,
                        addedQuestionId = null,
                        addedBarkochbaQuestionId = createEvent.questionId,
                    )
                )
            }
        }
        onEventHandled()
    }
}
