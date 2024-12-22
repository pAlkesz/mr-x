package com.palkesz.mr.x.feature.games.question.specify

import androidx.compose.foundation.layout.Column
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
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.input.PrimaryTextField
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.components.titlebar.CenteredTitleBar
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.helpers.QuestionMarkTransformation
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.games.game.ui.AnswerText
import com.palkesz.mr.x.feature.games.game.ui.QuestionText
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.expected_answer_label
import mrx.composeapp.generated.resources.ic_accepted_answer
import mrx.composeapp.generated.resources.ic_correct_answer
import mrx.composeapp.generated.resources.question_input_label
import mrx.composeapp.generated.resources.question_number_title
import mrx.composeapp.generated.resources.save_button_label
import mrx.composeapp.generated.resources.specify_question_input_error_message
import mrx.composeapp.generated.resources.specify_question_screen_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun SpecifyQuestionScreen(viewModel: SpecifyQuestionViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    SpecifyQuestionScreenContent(
        viewState = viewState,
        onTextChanged = viewModel::onTextChanged,
        onSaveClicked = viewModel::onSaveClicked,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun SpecifyQuestionScreenContent(
    viewState: ViewState<SpecifyQuestionViewState>,
    onTextChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onEventHandled: () -> Unit,
) {
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onSaveClicked) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        Column {
            CenteredTitleBar(title = stringResource(Res.string.specify_question_screen_title))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                QuestionDetails(
                    text = state.oldText,
                    hostAnswer = state.hostAnswer,
                    hostName = state.hostName,
                    expectedAnswer = state.expectedAnswer,
                    number = state.number,
                    owner = state.owner,
                )
                PrimaryTextField(
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                    value = state.text,
                    onValueChanged = onTextChanged,
                    isValueValid = state.isTextValid,
                    label = stringResource(Res.string.question_input_label),
                    error = stringResource(Res.string.specify_question_input_error_message),
                    visualTransformation = QuestionMarkTransformation,
                    showKeyboard = true,
                )
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    onClick = onSaveClicked,
                    enabled = state.isUpdateButtonEnabled,
                    text = stringResource(Res.string.save_button_label),
                )
            }
        }
    }
}

@Composable
private fun QuestionDetails(
    modifier: Modifier = Modifier,
    text: String,
    hostAnswer: String,
    hostName: String,
    expectedAnswer: String,
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
        QuestionText(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            text = text,
            owner = owner,
        )
        AnswerText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = hostAnswer,
            owner = hostName,
            isHost = true,
            icon = vectorResource(Res.drawable.ic_accepted_answer),
        )
        AnswerText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = stringResource(Res.string.expected_answer_label, expectedAnswer),
            owner = owner,
            isHost = false,
            icon = vectorResource(Res.drawable.ic_correct_answer),
        )
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun HandleEvent(event: SpecifyQuestionEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(event) { specifyEvent, _, _, navController ->
        when (specifyEvent) {
            is SpecifyQuestionEvent.NavigateUp -> {
                navController?.popBackStack()
            }
        }
        onEventHandled()
    }
}
