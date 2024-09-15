package com.mr.x.feature.games

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mr.x.core.ui.components.*
import com.mr.x.core.util.ViewState
import com.mr.x.core.util.fadingEdge
import com.mr.x.di.koinViewModel
import com.mr.x.feature.app.LocalAppState
import com.mr.x.feature.app.LocalNavController
import kotlinx.collections.immutable.toPersistentList
import mrx.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun MyGamesScreen(
	viewModel: MyGamesViewModel = koinViewModel<MyGamesViewModelImpl>()
) {
	val viewState by viewModel.viewState.collectAsState()
	MyGamesScreenContent(
		viewState = viewState,
		onRetry = viewModel::onRetry,
		onGameClicked = viewModel::onGameClicked,
		onShowQrCodeClicked = viewModel::onShowQrCodeClicked,
		onFilterSelected = viewModel::onFilterSelected,
		onEventHandled = viewModel::onEventHandled
	)
}

@Composable
fun MyGamesScreenContent(
	viewState: ViewState<MyGamesViewState>,
	onRetry: () -> Unit,
	onGameClicked: (String) -> Unit,
	onFilterSelected: (MyGamesFilter) -> Unit,
	onEventHandled: () -> Unit,
	onShowQrCodeClicked: (String) -> Unit
) {
	val appState = LocalAppState.current

	LaunchedEffect(Unit) {
		appState.apply {
			hideTopAppBar()
			showBottomAppBar()
			setScreenTitle(getString(Res.string.my_games))
		}
	}

	ContentWithBackgroundLoadingIndicator(
		state = viewState,
		onRetry = onRetry
	) { data ->
		HandleEvent(
			onEventHandled = onEventHandled,
			event = data.event
		)

		Box(modifier = Modifier.fillMaxSize()) {
			FilterDropdownMenu(
				modifier = Modifier.align(Alignment.TopEnd)
					.padding(vertical = 8.dp, horizontal = 24.dp)
					.zIndex(1f)
					.size(40.dp)
					.clip(RoundedCornerShape(8.dp))
					.border(
						width = 1.dp,
						color = MaterialTheme.colorScheme.secondary,
						shape = RoundedCornerShape(8.dp))
					.background(MaterialTheme.colorScheme.secondaryContainer),
				options = MyGamesFilter.entries.toPersistentList(),
				selected = data.selectedFilters,
				onSelected = onFilterSelected
			)
			CrossFade(condition = data.userGames.isEmpty(),
				onConditionTrue = {
					Box(
						modifier = Modifier.fillMaxSize(),
						contentAlignment = Alignment.Center,
					) {
						Text(
							text = if (data.selectedFilters.isEmpty()) {
								stringResource(Res.string.empty_games_list)
							}
							else {
								stringResource(Res.string.empty_games_with_filters)
							},
							modifier = Modifier.padding(16.dp))
					}
				},
				onConditionFalse = {
					LazyAnimatedColumn(
						items = data.userGames,
						keyProvider = { it.uuid },
						modifier = Modifier.fillMaxSize().fadingEdge(
							Brush.verticalGradient(
								0f to MaterialTheme.colorScheme.surface,
								0.1f to Color.Transparent
							)),
						lazyModifier = { Modifier.animateItemPlacement(tween(PLACEMENT_DURATION)) },
						enterTransition = AwaitedEnterTransition(ENTER_TRANSITION_DURATION) { duration ->
							slideInHorizontally(tween(duration)) { it } + fadeIn(tween(duration))
						},
						exitTransition = AwaitedExitTransition(EXIT_TRANSITION_DURATION) { duration ->
							slideOutHorizontally(tween(duration)) { it } + fadeOut(tween(duration))
						},
						horizontalAlignment = Alignment.CenterHorizontally,
						contentPadding = PaddingValues(top = 48.dp)
					) { _, game ->
						GameCard(
							defaultTitle = stringResource(Res.string.mr_x, game.initial),
							hiddenTitle = game.name,
							onQrCodeClick = { onShowQrCodeClicked(game.uuid) },
							onClick = { onGameClicked(game.uuid) },
							buttonLabel = stringResource(Res.string.enter_game),
							isHost = game.isHost,
							status = game.status
						)
					}
				}
			)
		}
	}
}

@Composable
fun HandleEvent(
	onEventHandled: () -> Unit,
	event: GameClickedEvent?
) {
	val navController = LocalNavController.current

	when (event) {
		is GameClickedEvent.GameClicked -> {
			navController?.navigate(GameScreenRoute.InGame.createRoute(event.uuid))
		}
		is GameClickedEvent.ShowQrCodeClicked -> {
			navController?.navigate(GameScreenRoute.GameQRCode.createRoute(event.uuid))
		}
		null -> return
	}
	onEventHandled()
}

private const val PLACEMENT_DURATION = 600
private const val ENTER_TRANSITION_DURATION = 700
private const val EXIT_TRANSITION_DURATION = 700
