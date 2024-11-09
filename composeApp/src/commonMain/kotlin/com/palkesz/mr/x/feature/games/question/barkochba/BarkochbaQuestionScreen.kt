package com.palkesz.mr.x.feature.games.question.barkochba

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
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
import com.palkesz.mr.x.core.ui.helpers.QuestionMarkTransformation
import com.palkesz.mr.x.core.ui.providers.LocalAppScope
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.ui.providers.LocalSnackBarHostState
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.enter_question
import mrx.composeapp.generated.resources.question
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BarkochbaQuestionScreen(
    viewModel: BarkochbaQuestionViewModel = koinViewModel<BarkochbaQuestionViewModelImpl>(),
    gameId: String
) {
    val viewState by viewModel.viewState.collectAsState()

    BarkochbaQuestionScreenContent(
        viewState = viewState,
        gameId = gameId,
        setGameId = viewModel::setGameId,
        onEventHandled = viewModel::onEventHandled,
        onTextChanged = viewModel::onTextChanged,
        onAskQuestionClicked = viewModel::onAskQuestionClicked
    )
}

@Composable
fun BarkochbaQuestionScreenContent(
    viewState: BarkochbaQuestionViewState,
    gameId: String,
    setGameId: (String) -> Unit,
    onEventHandled: () -> Unit,
    onTextChanged: (String) -> Unit,
    onAskQuestionClicked: () -> Unit
) {
    LaunchedEffect(Unit) {
        setGameId(gameId)
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
            imeAction = ImeAction.Done,
            keyboardCapitalization = KeyboardCapitalization.Sentences,
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                onAskQuestionClicked()
            }),
            isError = viewState.isTextInvalid,
            shape = RoundedCornerShape(
                topStartPercent = 100, topEndPercent = 5,
                bottomEndPercent = 100, bottomStartPercent = 5
            ),
            visualTransformation = QuestionMarkTransformation
        )
        DebouncedButton(
            onClick = {
                keyboardController?.hide()
                onAskQuestionClicked()
            }, shape = RoundedCornerShape(
                topStartPercent = 5, topEndPercent = 100,
                bottomEndPercent = 5, bottomStartPercent = 100
            ),
            modifier = Modifier.fillMaxWidth().padding(all = 12.dp)
        ) {
        }
    }
}

@Composable
fun HandleEvent(
    onEventHandled: () -> Unit,
    event: BarkochbaQuestionEvent?
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    when (event) {
        is BarkochbaQuestionEvent.ValidationError -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(event.message))
            }
        }

        is BarkochbaQuestionEvent.NavigateUp -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(event.message))
            }
        }

        null -> return
    }
    onEventHandled()
}
