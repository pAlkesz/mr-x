package com.palkesz.mr.x.feature.games.question.choose

import androidx.compose.foundation.layout.*
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
import com.palkesz.mr.x.core.ui.theme.AppTypography
import com.palkesz.mr.x.core.util.DebouncedButton
import com.palkesz.mr.x.di.koinViewModel
import com.palkesz.mr.x.feature.app.LocalAppScope
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.app.LocalSnackBarHostState
import com.palkesz.mr.x.feature.games.GameScreenRoute
import kotlinx.coroutines.launch
import mrx.mrx_app.generated.resources.*
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

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
	val appState = LocalAppState.current
	LaunchedEffect(Unit) {
		setGameId(gameId)
		appState.apply {
			setScreenTitle(getString(Res.string.ask_question))
			showTopAppBar()
			hideBottomAppBar()
		}
	}

	HandleEvent(
		onEventHandled = onEventHandled,
		event = viewState.event
	)

	Column(
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.fillMaxSize()) {
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
				style = AppTypography.headlineMedium,
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
				style = AppTypography.headlineMedium,
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
			GameScreenRoute.BarkochbaQuestion.createRoute(event.gameId))
		is ChooseQuestionEvent.NormalQuestionClicked -> navController?.navigate(
			GameScreenRoute.NormalQuestion.createRoute(event.gameId))
		null -> return
		is ChooseQuestionEvent.NavigateUp -> {
			LocalAppScope.current?.launch {
				snackbarHostState.showSnackbar(message = getString(event.message))
			}
			navController?.popBackStack(GameScreenRoute.InGame.createRoute(event.gameId), false)
		}
	}
	onEventHandled()
}
