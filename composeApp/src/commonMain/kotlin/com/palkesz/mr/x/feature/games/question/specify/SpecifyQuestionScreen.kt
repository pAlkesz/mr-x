package com.palkesz.mr.x.feature.games.question.specify

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.BaseTextField
import com.palkesz.mr.x.core.ui.components.ChangeableText
import com.palkesz.mr.x.core.ui.components.DebouncedButton
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.helpers.QuestionMarkTransformation
import com.palkesz.mr.x.core.ui.providers.LocalAppScope
import com.palkesz.mr.x.core.ui.providers.LocalAppState
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.ui.providers.LocalSnackBarHostState
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.games.GameGraphRoute
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_question
import mrx.composeapp.generated.resources.enter_question
import mrx.composeapp.generated.resources.question
import mrx.composeapp.generated.resources.show_hint_text
import mrx.composeapp.generated.resources.specify_question
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpecifyQuestionScreen(
    viewModel: SpecifyQuestionViewModel = koinViewModel<SpecifyQuestionViewModelImpl>(),
    questionId: String,
    gameId: String
) {
    val viewState by viewModel.viewState.collectAsState()

    SpecifyQuestionScreenContent(
        viewState = viewState,
        questionId = questionId,
        gameId = gameId,
        setQuestionId = viewModel::setVariablesFromNavigation,
        onEventHandled = viewModel::onEventHandled,
        onTextChanged = viewModel::onTextChanged,
        onAskQuestionClicked = viewModel::onAskQuestionClicked,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun SpecifyQuestionScreenContent(
    viewState: ViewState<SpecifyQuestionViewState>,
    questionId: String,
    gameId: String,
    setQuestionId: (String, String) -> Unit,
    onEventHandled: () -> Unit,
    onTextChanged: (String) -> Unit,
    onAskQuestionClicked: () -> Unit,
    onRetry: () -> Unit
) {
    val appState = LocalAppState.current
    LaunchedEffect(Unit) {
        setQuestionId(questionId, gameId)
        appState.apply {
            setScreenTitle(getString(Res.string.specify_question))
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
                text = "${data.oldQuestionText}?",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            BaseTextField(
                modifier = Modifier.fillMaxWidth(),
                value = data.text,
                onValueChange = {
                    onTextChanged(it)
                },
                label = {
                    Text(text = stringResource(Res.string.question))
                },
                placeholder = {
                    Text(text = stringResource(Res.string.enter_question))
                },
                keyboardCapitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next,
                isError = data.isTextInvalid,
                shape = RoundedCornerShape(
                    topStartPercent = 5, topEndPercent = 100,
                    bottomEndPercent = 5, bottomStartPercent = 100
                ),
                visualTransformation = QuestionMarkTransformation
            )
            DebouncedButton(
                onClick = {
                    keyboardController?.hide()
                    onAskQuestionClicked()
                }, shape = RoundedCornerShape(
                    topStartPercent = 100, topEndPercent = 5,
                    bottomEndPercent = 100, bottomStartPercent = 5
                ),
                modifier = Modifier.fillMaxWidth().padding(all = 12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.ask_question),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            ChangeableText(
                defaultText = stringResource(Res.string.show_hint_text),
                hiddenText = data.expectedAnswer,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
fun HandleEvent(
    onEventHandled: () -> Unit,
    event: SpecifyQuestionEvent?
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    when (event) {
        is SpecifyQuestionEvent.ValidationError -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(event.message))
            }
        }

        is SpecifyQuestionEvent.NavigateUp -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(event.message))
            }
            navController?.popBackStack(GameGraphRoute.InGame.createRoute(event.gameId), false)
        }

        null -> return
    }
    onEventHandled()
}
