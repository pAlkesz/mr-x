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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.input.PrimaryTextField
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.components.text.buildWrongHostAnswer
import com.palkesz.mr.x.core.ui.components.titlebar.CenteredTitleBar
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.util.extensions.capitalizeFirstChar
import com.palkesz.mr.x.core.util.networking.ViewState
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.answer_question_button_label
import mrx.composeapp.generated.resources.expected_last_name_input_label
import mrx.composeapp.generated.resources.first_name_error_message
import mrx.composeapp.generated.resources.first_name_input_label
import mrx.composeapp.generated.resources.guess_question_screen_title
import mrx.composeapp.generated.resources.last_name_error_message
import mrx.composeapp.generated.resources.question_number_title
import mrx.composeapp.generated.resources.question_text
import org.jetbrains.compose.resources.stringResource

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
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onAnswerClicked) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        Column {
            CenteredTitleBar(title = stringResource(Res.string.guess_question_screen_title))
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
                        error = stringResource(Res.string.first_name_error_message),
                        showKeyboard = true,
                    )
                    PrimaryTextField(
                        modifier = Modifier.weight(1f),
                        value = state.lastName,
                        onValueChanged = onLastNameChanged,
                        isValueValid = state.isLastNameValid,
                        label = stringResource(Res.string.expected_last_name_input_label),
                        error = stringResource(Res.string.last_name_error_message),
                        showKeyboard = false,
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
}

@Composable
private fun QuestionDetails(
    modifier: Modifier = Modifier,
    text: String,
    hostAnswer: String?,
    number: Int,
    owner: String,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            text = stringResource(Res.string.question_number_title, number, owner),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            text = stringResource(Res.string.question_text, text.capitalizeFirstChar()),
            style = MaterialTheme.typography.bodyMedium,
        )
        AnimatedNullability(hostAnswer) { answer ->
            Text(
                text = buildWrongHostAnswer(answer = answer),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun HandleEvent(event: GuessQuestionEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(event) { guessEvent, _, _, navController ->
        when (guessEvent) {
            is GuessQuestionEvent.NavigateUp -> {
                navController?.popBackStack()
            }
        }
        onEventHandled()
    }
}
