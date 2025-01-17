package com.palkesz.mr.x.feature.games.question.guess

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.input.PrimaryTextField
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import com.palkesz.mr.x.feature.games.GameGraph
import com.palkesz.mr.x.feature.games.game.ui.AnswerText
import com.palkesz.mr.x.feature.games.game.ui.QuestionText
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.answer_question_button_label
import mrx.composeapp.generated.resources.expected_last_name_input_label
import mrx.composeapp.generated.resources.first_name_input_label
import mrx.composeapp.generated.resources.guess_question_screen_title
import mrx.composeapp.generated.resources.ic_wrong_answer
import mrx.composeapp.generated.resources.question_first_name_error_message
import mrx.composeapp.generated.resources.question_last_name_error_message
import mrx.composeapp.generated.resources.question_number_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun GuessQuestionScreen(viewModel: GuessQuestionViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    GuessQuestionScreenContent(
        viewState = viewState,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onLastNameChanged = viewModel::onLastNameChanged,
        onAnswerClicked = viewModel::onAnswerClicked,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun GuessQuestionScreenContent(
    viewState: ViewState<GuessQuestionViewState>,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onAnswerClicked: () -> Unit,
    onEventHandled: () -> Unit,
) {
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.guess_question_screen_title)
        )
    )
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onAnswerClicked) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            QuestionDetails(
                text = state.questionText,
                hostAnswer = state.hostAnswer,
                hostName = state.hostName,
                number = state.number,
                owner = state.owner,
            )
            Row(modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)) {
                PrimaryTextField(
                    modifier = Modifier.padding(end = 16.dp).weight(1f),
                    value = state.firstName,
                    onValueChanged = onFirstNameChanged,
                    isValueValid = state.isFirstNameValid,
                    label = stringResource(Res.string.first_name_input_label),
                    error = stringResource(Res.string.question_first_name_error_message),
                    imeAction = ImeAction.Next,
                    showKeyboard = true,
                )
                PrimaryTextField(
                    modifier = Modifier.weight(1f),
                    value = state.lastName,
                    onValueChanged = onLastNameChanged,
                    isValueValid = state.isLastNameValid,
                    label = stringResource(Res.string.expected_last_name_input_label),
                    error = stringResource(Res.string.question_last_name_error_message),
                    imeAction = ImeAction.Send,
                    showKeyboard = false,
                    onSend = { onAnswerClicked() },
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                onClick = onAnswerClicked,
                enabled = state.isAnswerButtonEnabled,
                text = stringResource(Res.string.answer_question_button_label),
            )
        }
    }
}

@Composable
private fun QuestionDetails(
    modifier: Modifier = Modifier,
    text: String,
    hostAnswer: String?,
    hostName: String,
    number: Int,
    owner: String,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            text = stringResource(Res.string.question_number_title, number),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text,
            owner = owner,
        )
        AnimatedNullability(hostAnswer) { answer ->
            AnswerText(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = answer,
                owner = hostName,
                isHost = true,
                icon = vectorResource(Res.drawable.ic_wrong_answer),
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun HandleEvent(event: GuessQuestionEvent?, onEventHandled: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    HandleEventEffect(event) { guessEvent, _, _, navController ->
        when (guessEvent) {
            is GuessQuestionEvent.NavigateUp -> {
                keyboardController?.hide()
                navController?.navigate(
                    route = GameGraph.Game(
                        id = guessEvent.gameId,
                        tabIndex = 0,
                        addedQuestionId = null,
                        addedBarkochbaQuestionId = null,
                    )
                ) {
                    popUpTo<GameGraph.Game> {
                        inclusive = true
                    }
                }
            }
        }
        onEventHandled()
    }
}
