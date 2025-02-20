package com.palkesz.mr.x.feature.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.ui.components.animation.AnimatedLazyColumn
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.badge.ContentWithBadge
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.ui.helpers.bold
import com.palkesz.mr.x.core.ui.modifiers.conditional
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import com.palkesz.mr.x.feature.games.ui.GameIcon
import com.palkesz.mr.x.feature.games.ui.GameQuestionChip
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.game_host_label
import mrx.composeapp.generated.resources.game_title_with_initial
import mrx.composeapp.generated.resources.games_screen_title
import mrx.composeapp.generated.resources.ic_chevron_right
import mrx.composeapp.generated.resources.ic_host
import mrx.composeapp.generated.resources.ic_player
import mrx.composeapp.generated.resources.no_games_message
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun GamesScreen(viewModel: GamesViewModel) {
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
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.games_screen_title),
            navigationIcon = null,
        )
    )
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        CrossFade(
            condition = state.games.isEmpty(),
            onConditionTrue = {
                Box(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.no_games_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            onConditionFalse = {
                AnimatedLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    items = state.games,
                    animatedItemKey = state.joinedGameId,
                    getKey = { id },
                ) { index, item ->
                    GameCard(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .conditional(condition = index != state.games.lastIndex) {
                                padding(bottom = 16.dp)
                            }
                            .conditional(condition = index == 0) {
                                padding(top = 4.dp)
                            },
                        item = item,
                        onClick = onGameClicked,
                    )
                }
            }
        )
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
    ContentWithBadge(
        modifier = modifier,
        badgeCount = item.badgeCount,
        isBadgeBig = true,
        badgeOffset = DpOffset(x = 4.dp, y = (-4).dp),
    ) {
        Card(
            modifier = Modifier.widthIn(max = 488.dp),
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
                Column(modifier = Modifier.padding(end = 16.dp).weight(weight = 1f)) {
                    Text(
                        text = stringResource(Res.string.game_title_with_initial, item.initial),
                        style = MaterialTheme.typography.bodyMedium.bold()
                    )
                    AnimatedNullability(item = item.hostName) { name ->
                        Text(
                            text = stringResource(Res.string.game_host_label, name),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                GameQuestionChip(
                    questionCount = item.questionCount,
                    barkochbaCount = item.barkochbaCount,
                    containerColor = containerColor,
                )
                Icon(
                    modifier = Modifier.padding(start = 16.dp),
                    imageVector = vectorResource(Res.drawable.ic_chevron_right),
                    tint = LocalContentColor.current,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun HandleEvent(event: GamesEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(event) { gameEvent, _, _, navController ->
        when (gameEvent) {
            is GamesEvent.NavigateToGame -> {
                navController?.navigate(
                    GameGraph.Game(
                        id = gameEvent.id,
                        tabIndex = 0,
                        addedQuestionId = null,
                        addedBarkochbaQuestionId = null,
                    )
                )
            }
        }
        onEventHandled()
    }
}
