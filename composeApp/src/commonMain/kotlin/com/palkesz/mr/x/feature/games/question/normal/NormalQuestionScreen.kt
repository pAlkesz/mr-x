package com.palkesz.mr.x.feature.games.question.normal

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
import com.palkesz.mr.x.core.ui.components.DebouncedButton
import com.palkesz.mr.x.core.ui.components.FirstAndLastNameTexFields
import com.palkesz.mr.x.core.ui.helpers.QuestionMarkTransformation
import com.palkesz.mr.x.core.util.di.koinViewModel
import com.palkesz.mr.x.feature.app.LocalAppScope
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.app.LocalSnackBarHostState
import com.palkesz.mr.x.feature.games.GameScreenRoute
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_question
import mrx.composeapp.generated.resources.enter_question
import mrx.composeapp.generated.resources.expected_answer
import mrx.composeapp.generated.resources.normal_question
import mrx.composeapp.generated.resources.question
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun NormalQuestionScreen(
    viewModel: NormalQuestionViewModel = koinViewModel<NormalQuestionViewModelImpl>(),
    gameId: String
) {
    val viewState by viewModel.viewState.collectAsState()

    NormalQuestionScreenContent(
        viewState = viewState,
        gameId = gameId,
        setGameId = viewModel::setGameId,
        onEventHandled = viewModel::onEventHandled,
        onTextChanged = viewModel::onTextChanged,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onLastNameChanged = viewModel::onLastNameChanged,
        onAskQuestionClicked = viewModel::onAskQuestionClicked
    )
}

@Composable
fun NormalQuestionScreenContent(
    viewState: NormalQuestionViewState,
    gameId: String,
    setGameId: (String) -> Unit,
    onEventHandled: () -> Unit,
    onTextChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onAskQuestionClicked: () -> Unit
) {
    val appState = LocalAppState.current
    LaunchedEffect(Unit) {
        setGameId(gameId)
        appState.apply {
            setScreenTitle(getString(Res.string.normal_question))
            showTopAppBar()
            hideBottomAppBar()
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    HandleEvent(
        onEventHandled = onEventHandled,
        event = viewState.event
    )

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewState.text,
            onValueChange = {
                onTextChanged(it)
            },
            label = {
                Text(text = stringResource(Res.string.question))
            },
            placeholder = {
                Text(text = stringResource(Res.string.enter_question))
            },
            imeAction = ImeAction.Next,
            keyboardCapitalization = KeyboardCapitalization.Sentences,
            isError = viewState.isTextInvalid,
            shape = RoundedCornerShape(
                topStartPercent = 5, topEndPercent = 100,
                bottomEndPercent = 5, bottomStartPercent = 100
            ),
            visualTransformation = QuestionMarkTransformation
        )
        Text(
            text = stringResource(Res.string.expected_answer),
            modifier = Modifier.padding(start = 36.dp, top = 12.dp).align(Alignment.Start),
            style = MaterialTheme.typography.headlineSmall
        )
        FirstAndLastNameTexFields(
            firstName = viewState.firstName,
            lastName = viewState.lastName,
            isFirstNameInvalid = viewState.isFirstNameInvalid,
            isLastNameInvalid = viewState.isLastNameInvalid,
            onFirstNameChanged = onFirstNameChanged,
            onLastNameChanged = onLastNameChanged,
            onDone = {
                keyboardController?.hide()
                onAskQuestionClicked()
            }
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
    }
}

@Composable
fun HandleEvent(
    onEventHandled: () -> Unit,
    event: NormalQuestionEvent?
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    when (event) {
        is NormalQuestionEvent.ValidationError -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(event.message))
            }
        }

        is NormalQuestionEvent.NavigateUp -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(event.message))
            }
            navController?.popBackStack(GameScreenRoute.InGame.createRoute(event.gameId), false)
        }

        null -> return
    }
    onEventHandled()
}
