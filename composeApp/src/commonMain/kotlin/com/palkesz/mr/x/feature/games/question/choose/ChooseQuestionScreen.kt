package com.palkesz.mr.x.feature.games.question.choose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.DebouncedButton
import com.palkesz.mr.x.core.ui.providers.LocalAppScope
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.ui.providers.LocalSnackBarHostState
import com.palkesz.mr.x.feature.games.GameGraphRoute
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.barkochba_question
import mrx.composeapp.generated.resources.barkochba_question_count
import mrx.composeapp.generated.resources.normal_question
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChooseQuestionScreen(
    viewModel: ChooseQuestionViewModel = koinViewModel<ChooseQuestionViewModelImpl>(),
    gameId: String
) {
    val viewState by viewModel.viewState.collectAsState()

    ChooseQuestionScreenContent(
        viewState = viewState,
        gameId = gameId,
        setGameId = viewModel::setGameId,
        onNormalQuestionClicked = viewModel::onNormalQuestionClicked,
        onBarkochbaQuestionClicked = viewModel::onBarkochbaQuestionClicked,
        onEventHandled = viewModel::onEventHandled
    )
}

@Composable
fun ChooseQuestionScreenContent(
    viewState: ChooseQuestionViewState,
    gameId: String,
    setGameId: (String) -> Unit,
    onNormalQuestionClicked: () -> Unit,
    onBarkochbaQuestionClicked: () -> Unit,
    onEventHandled: () -> Unit
) {
    LaunchedEffect(Unit) {
        setGameId(gameId)
    }

    HandleEvent(
        onEventHandled = onEventHandled,
        event = viewState.event
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        DebouncedButton(
            onClick = onNormalQuestionClicked,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(Res.string.normal_question),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
        Text(
            text = stringResource(Res.string.barkochba_question_count, viewState.barkochbaCount),
            modifier = Modifier.padding(top = 80.dp, bottom = 16.dp)
        )
        DebouncedButton(
            onClick = onBarkochbaQuestionClicked,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = viewState.barkochbaCount != 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(Res.string.barkochba_question),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}

@Composable
fun HandleEvent(
    onEventHandled: () -> Unit,
    event: ChooseQuestionEvent?
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    when (event) {
        is ChooseQuestionEvent.BarkochbaQuestionClicked -> navController?.navigate(
            GameGraphRoute.BarkochbaQuestion.createRoute(event.gameId)
        )

        is ChooseQuestionEvent.NormalQuestionClicked -> navController?.navigate(
            GameGraphRoute.NormalQuestion.createRoute(event.gameId)
        )

        null -> return
        is ChooseQuestionEvent.NavigateUp -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(event.message))
            }
            navController?.popBackStack(GameGraphRoute.InGame.createRoute(event.gameId), false)
        }
    }
    onEventHandled()
}
