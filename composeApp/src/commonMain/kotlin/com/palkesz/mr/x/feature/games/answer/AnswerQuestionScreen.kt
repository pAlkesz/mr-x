package com.palkesz.mr.x.feature.games.answer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.DebouncedButton
import com.palkesz.mr.x.core.ui.components.FirstAndLastNameTexFields
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.LocalAppScope
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.app.LocalSnackBarHostState
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.answer_question
import mrx.composeapp.generated.resources.answer_screen_label
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnswerQuestionScreen(
    questionId: String,
    gameId: String,
    isHost: Boolean,
    viewModel: AnswerQuestionViewModel = koinViewModel<AnswerQuestionViewModelImpl>()
) {
    val viewState by viewModel.viewState.collectAsState()

    AnswerQuestionScreenContent(
        viewState = viewState,
        questionId = questionId,
        gameId = gameId,
        isHost = isHost,
        setNavArguments = viewModel::setNavArguments,
        onEventHandled = viewModel::onEventHandled,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onLastNameChanged = viewModel::onLastNameChanged,
        onAnswerQuestionClicked = viewModel::onAnswerQuestionClicked,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun AnswerQuestionScreenContent(
    viewState: ViewState<AnswerQuestionViewState>,
    questionId: String,
    gameId: String,
    isHost: Boolean,
    setNavArguments: (String, String, Boolean) -> Unit,
    onEventHandled: () -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onAnswerQuestionClicked: () -> Unit,
    onRetry: () -> Unit
) {
    val appState = LocalAppState.current
    LaunchedEffect(Unit) {
        setNavArguments(questionId, gameId, isHost)
        appState.apply {
            setScreenTitle(getString(Res.string.answer_question))
            showTopAppBar()
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry)
    { data ->
        HandleEvent(
            onEventHandled = onEventHandled,
            event = data.event
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${data.questionText}?",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(20.dp)
            )
            AnimatedVisibility(isHost) {
                Text(
                    text = stringResource(Res.string.answer_screen_label),
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            FirstAndLastNameTexFields(
                firstName = data.firstName,
                lastName = data.lastName,
                isFirstNameInvalid = data.isFirstNameInvalid,
                isLastNameInvalid = data.isLastNameInvalid,
                onFirstNameChanged = onFirstNameChanged,
                onLastNameChanged = onLastNameChanged,
                onDone = {
                    keyboardController?.hide()
                    onAnswerQuestionClicked()
                }
            )
            DebouncedButton(
                onClick = {
                    keyboardController?.hide()
                    onAnswerQuestionClicked()
                }, shape = RoundedCornerShape(
                    topStartPercent = 100, topEndPercent = 5,
                    bottomEndPercent = 100, bottomStartPercent = 5
                ),
                modifier = Modifier.fillMaxWidth().padding(all = 12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.answer_question),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
fun HandleEvent(
    onEventHandled: () -> Unit,
    event: AnswerQuestionEvent?
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    when (event) {
        is AnswerQuestionEvent.ValidationError -> LocalAppScope.current?.launch {
            snackbarHostState.showSnackbar(message = getString(event.message))
        }

        is AnswerQuestionEvent.NavigateUp -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(event.message))
            }
            navController?.navigateUp()
        }

        null -> return
    }
    onEventHandled()
}
