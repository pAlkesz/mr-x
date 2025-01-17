package com.palkesz.mr.x.feature.games.question.specify

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.ui.helpers.QuestionMarkTransformation
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import com.palkesz.mr.x.feature.games.GameGraph
import com.palkesz.mr.x.feature.games.game.ui.AnswerText
import com.palkesz.mr.x.feature.games.game.ui.ExpectedAnswerText
import com.palkesz.mr.x.feature.games.game.ui.QuestionText
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_accepted_answer
import mrx.composeapp.generated.resources.ic_send
import mrx.composeapp.generated.resources.question_number_title
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
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.specify_question_screen_title)
        )
    )
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onSaveClicked) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
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
            Spacer(modifier = Modifier.weight(1f))
            QuestionInputField(
                modifier = Modifier.padding(top = 32.dp),
                text = state.text,
                onTextChanged = onTextChanged,
                isTextValid = state.isTextValid,
                isSaveButtonEnabled = state.isSaveButtonEnabled,
                onSaveClicked = onSaveClicked,
            )
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
        ExpectedAnswerText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = expectedAnswer,
            owner = owner,
        )
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun QuestionInputField(
    modifier: Modifier = Modifier,
    text: String,
    isTextValid: Boolean,
    isSaveButtonEnabled: Boolean,
    onTextChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = tween(
                easing = LinearEasing,
                durationMillis = TEXT_ERROR_ANIMATION_LENGTH,
            )
        )
    ) {
        Row(modifier = modifier.widthIn(max = 488.dp).fillMaxWidth()) {
            TextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .weight(weight = 1f)
                    .padding(end = 16.dp),
                value = text,
                onValueChange = onTextChanged,
                isError = !isTextValid,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send,
                ),
                keyboardActions = KeyboardActions(onSend = { onSaveClicked() }),
                visualTransformation = QuestionMarkTransformation,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
                shape = CircleShape,
            )
            SaveButton(enabled = isSaveButtonEnabled, onClick = onSaveClicked)
        }
        AnimatedVisibility(!isTextValid) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                text = stringResource(Res.string.specify_question_input_error_message),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun SaveButton(modifier: Modifier = Modifier, enabled: Boolean, onClick: () -> Unit) {
    val iconColor by animateColorAsState(
        targetValue = if (enabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        }
    )
    Box(
        modifier = modifier
            .size(size = 56.dp)
            .clip(shape = CircleShape)
            .clickable(enabled = enabled) { onClick() }
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_send),
            tint = iconColor,
            contentDescription = null,
        )
    }
}

@Composable
private fun HandleEvent(event: SpecifyQuestionEvent?, onEventHandled: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    HandleEventEffect(event) { specifyEvent, _, _, navController ->
        when (specifyEvent) {
            is SpecifyQuestionEvent.NavigateUp -> {
                keyboardController?.hide()
                navController?.navigate(
                    route = GameGraph.Game(
                        id = specifyEvent.gameId,
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

private const val TEXT_ERROR_ANIMATION_LENGTH = 100
