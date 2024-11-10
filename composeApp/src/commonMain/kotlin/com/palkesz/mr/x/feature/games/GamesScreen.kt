package com.palkesz.mr.x.feature.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.components.titlebar.CenteredTitleBar
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.util.networking.ViewState
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.game_title_with_initial
import mrx.composeapp.generated.resources.games_screen_title
import mrx.composeapp.generated.resources.ic_chevron_right
import mrx.composeapp.generated.resources.ic_host
import mrx.composeapp.generated.resources.ic_player
import mrx.composeapp.generated.resources.no_games_message
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GamesScreen(viewModel: GamesViewModel = koinViewModel<GamesViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    GamesScreenContent(
        viewState = viewState,
        onRetry = viewModel::onRetry,
        onGameClicked = viewModel::onGameClicked,
        onEventHandled = viewModel::onEventHandled
    )
}

@Composable
private fun GamesScreenContent(
    viewState: ViewState<GamesViewState>,
    onRetry: () -> Unit,
    onGameClicked: (String) -> Unit,
    onEventHandled: () -> Unit,
) {
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            CenteredTitleBar(
                title = stringResource(Res.string.games_screen_title),
                navigationIcon = null,
            )
            CrossFade(
                condition = state.games.isEmpty(),
                onConditionTrue = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(Res.string.no_games_message),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                    }
                },
                onConditionFalse = {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.games, key = { it.id }) { item ->
                            GameCard(
                                modifier = Modifier.animateItem().padding(bottom = 16.dp),
                                item = item,
                                onClick = onGameClicked,
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun GameCard(modifier: Modifier = Modifier, item: GameItem, onClick: (String) -> Unit) {
    val containerColor by animateColorAsState(
        targetValue = if (item.status == GameStatus.ONGOING) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }
    )
    Card(
        modifier = modifier,
        onClick = { onClick(item.id) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GameIcon(
                modifier = Modifier.padding(end = 16.dp),
                icon = vectorResource(if (item.isHost) Res.drawable.ic_host else Res.drawable.ic_player),
            )
            Text(
                text = stringResource(Res.string.game_title_with_initial, item.initial),
                modifier = Modifier.padding(end = 16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = vectorResource(Res.drawable.ic_chevron_right),
                tint = LocalContentColor.current,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun GameIcon(modifier: Modifier = Modifier, icon: ImageVector) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSecondary,
            contentDescription = null,
        )
    }
}

@Composable
private fun HandleEvent(event: GamesEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(event) { gameEvent, _, _, navController ->
        when (gameEvent) {
            is GamesEvent.NavigateToGame -> {
                navController?.navigate(GameGraph.Game(gameEvent.id))
            }
        }
        onEventHandled()
    }
}
