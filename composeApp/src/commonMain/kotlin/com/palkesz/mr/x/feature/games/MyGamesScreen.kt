package com.palkesz.mr.x.feature.games

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.palkesz.mr.x.core.ui.components.AwaitedEnterTransition
import com.palkesz.mr.x.core.ui.components.AwaitedExitTransition
import com.palkesz.mr.x.core.ui.components.FilterDropdownMenu
import com.palkesz.mr.x.core.ui.components.GameCard
import com.palkesz.mr.x.core.ui.components.LazyAnimatedColumn
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.modifiers.fadingEdge
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.util.networking.ViewState
import kotlinx.collections.immutable.toPersistentList
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.empty_games_list
import mrx.composeapp.generated.resources.empty_games_with_filters
import mrx.composeapp.generated.resources.enter_game
import mrx.composeapp.generated.resources.mr_x
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
                        shape = RoundedCornerShape(8.dp)
                    )
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
                            } else {
                                stringResource(Res.string.empty_games_with_filters)
                            },
                            modifier = Modifier.padding(16.dp)
                        )
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
                            )
                        ),
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
            navController?.navigate(GameGraphRoute.InGame.createRoute(event.uuid))
        }

        is GameClickedEvent.ShowQrCodeClicked -> {
            navController?.navigate(GameGraphRoute.GameQRCode.createRoute(event.uuid))
        }

        null -> return
    }
    onEventHandled()
}

private const val PLACEMENT_DURATION = 600
private const val ENTER_TRANSITION_DURATION = 700
private const val EXIT_TRANSITION_DURATION = 700
