package com.palkesz.mr.x.feature.games.gameDetailsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.ui.components.AwaitedEnterTransition
import com.palkesz.mr.x.core.ui.components.AwaitedExitTransition
import com.palkesz.mr.x.core.ui.components.CustomRoundedCornerShape
import com.palkesz.mr.x.core.ui.components.DebouncedButton
import com.palkesz.mr.x.core.ui.components.FilterDropdownMenu
import com.palkesz.mr.x.core.ui.components.LazyAnimatedColumn
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.modifiers.conditional
import com.palkesz.mr.x.core.ui.modifiers.fadingEdge
import com.palkesz.mr.x.core.ui.providers.LocalAppState
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.ui.theme.MrXTheme
import com.palkesz.mr.x.core.ui.theme.errorContainer
import com.palkesz.mr.x.core.ui.theme.errorContainerLight
import com.palkesz.mr.x.core.ui.theme.onErrorContainerLight
import com.palkesz.mr.x.core.ui.theme.onPrimaryContainerLight
import com.palkesz.mr.x.core.ui.theme.onTertiaryContainerLight
import com.palkesz.mr.x.core.ui.theme.primaryContainerLight
import com.palkesz.mr.x.core.ui.theme.surfaceLight
import com.palkesz.mr.x.core.ui.theme.tertiaryContainerLight
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.getOrNull
import com.palkesz.mr.x.feature.games.GameGraphRoute
import kotlinx.collections.immutable.toPersistentList
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.accept_button_label
import mrx.composeapp.generated.resources.answer_button_label
import mrx.composeapp.generated.resources.ask_question
import mrx.composeapp.generated.resources.correct_answer
import mrx.composeapp.generated.resources.correct_answer_template
import mrx.composeapp.generated.resources.correct_answer_text
import mrx.composeapp.generated.resources.decline_button_label
import mrx.composeapp.generated.resources.empty_list_with_filters
import mrx.composeapp.generated.resources.false_button_label
import mrx.composeapp.generated.resources.game_over
import mrx.composeapp.generated.resources.host
import mrx.composeapp.generated.resources.host_answer_template
import mrx.composeapp.generated.resources.host_guessed_for_host_label
import mrx.composeapp.generated.resources.host_guessed_label
import mrx.composeapp.generated.resources.mr_x
import mrx.composeapp.generated.resources.no
import mrx.composeapp.generated.resources.no_questions_have_been_asked
import mrx.composeapp.generated.resources.pass_button_label
import mrx.composeapp.generated.resources.player_answer_template
import mrx.composeapp.generated.resources.true_button_label
import mrx.composeapp.generated.resources.waiting_for_host_label
import mrx.composeapp.generated.resources.waiting_for_owner_label
import mrx.composeapp.generated.resources.waiting_for_players_label
import mrx.composeapp.generated.resources.waiting_for_players_to_answer
import mrx.composeapp.generated.resources.waiting_label
import mrx.composeapp.generated.resources.wrong_answer_label
import mrx.composeapp.generated.resources.yes
import mrx.composeapp.generated.resources.yes_or_no_text
import mrx.composeapp.generated.resources.your_up_label
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameDetailsScreen(
    viewModel: GamesDetailViewModel = koinViewModel<GamesDetailViewModel>(),
    gameId: String
) {
    val viewState by viewModel.viewState.collectAsState()

    GameDetailsScreenContent(
        viewState = viewState,
        gameId = gameId,
        onAskQuestionClicked = viewModel::onAskQuestionClicked,
        setGameId = viewModel::setGameId,
        onEventHandled = viewModel::onEventHandled,
        onPlayerAnswerClick = viewModel::onPlayerAnswerClicked,
        onHostPassClicked = viewModel::onHostPassedClicked,
        onBarkochbaAnswered = viewModel::onBarkochbaAnswered,
        onAcceptHostAnswerClicked = viewModel::onAcceptHostAnswerClicked,
        onDeclineHostAnswerClicked = viewModel::onDeclineHostAnswerClicked,
        onRetry = viewModel::onRetry,
        onFilterSelected = viewModel::onFilterSelected
    )
}

@Composable
fun GameDetailsScreenContent(
    viewState: ViewState<GameDetailsViewState>,
    gameId: String,
    onAskQuestionClicked: () -> Unit,
    setGameId: (String) -> Unit,
    onEventHandled: () -> Unit,
    onAcceptHostAnswerClicked: (String, String) -> Unit,
    onDeclineHostAnswerClicked: (String) -> Unit,
    onPlayerAnswerClick: (String, String, Boolean) -> Unit,
    onHostPassClicked: (String) -> Unit,
    onBarkochbaAnswered: (Boolean, String) -> Unit,
    onRetry: () -> Unit,
    onFilterSelected: (QuestionItemFilter) -> Unit
) {
    val appState = LocalAppState.current
    LaunchedEffect(Unit) {
        setGameId(gameId)
    }

    LaunchedEffect(viewState.getOrNull()?.defaultTitle) {
        appState.apply {
            viewState.getOrNull()?.let { viewState ->
                setScreenTitle(
                    title = getString(Res.string.mr_x, viewState.defaultTitle),
                    optionalHiddenTitle = viewState.optionalHiddenTitle
                )
            }
        }
    }

    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { data ->
        HandleEvent(
            onEventHandled = onEventHandled,
            event = data.event
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.host, data.hostName),
                        modifier = Modifier.padding(16.dp)
                    )
                    FilterDropdownMenu(
                        onSelected = onFilterSelected,
                        selected = data.selectedFilters,
                        options = QuestionItemFilter.entries.toPersistentList(),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                CrossFade(
                    condition = data.questions.isEmpty(),
                    modifier = Modifier.weight(1f),
                    onConditionTrue = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (data.selectedFilters.isEmpty()) {
                                    stringResource(Res.string.no_questions_have_been_asked)
                                } else {
                                    stringResource(Res.string.empty_list_with_filters)
                                },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    },
                    onConditionFalse = {
                        LazyAnimatedColumn(
                            items = data.questions,
                            keyProvider = { it.uuid },
                            modifier = Modifier.fillMaxWidth()
                                .conditional(data.isAskQuestionButtonVisible) {
                                    fadingEdge(
                                        Brush.verticalGradient(
                                            0.9f to Color.Transparent,
                                            1f to surfaceLight
                                        )
                                    )
                                },
                            lazyModifier = { Modifier.animateItemPlacement(tween(PLACEMENT_DURATION)) },
                            enterTransition = AwaitedEnterTransition(ENTER_TRANSITION_DURATION) { duration ->
                                slideInHorizontally(tween(duration)) { it } + fadeIn(tween(duration))
                            },
                            exitTransition = AwaitedExitTransition(EXIT_TRANSITION_DURATION) { duration ->
                                slideOutHorizontally(tween(duration)) { it } + fadeOut(
                                    tween(
                                        duration
                                    )
                                )
                            },
                            contentPadding = PaddingValues(
                                start = 4.dp,
                                end = 4.dp,
                                top = 4.dp,
                                bottom =
                                if (data.isAskQuestionButtonVisible) {
                                    76.dp
                                } else {
                                    8.dp
                                }
                            )
                        ) { _, question ->
                            QuestionItem(
                                question = question,
                                playerIsHost = data.playerIsHost,
                                onAcceptHostAnswerClicked = onAcceptHostAnswerClicked,
                                onDeclineHostAnswerClicked = onDeclineHostAnswerClicked,
                                onPlayerAnswerClick = onPlayerAnswerClick,
                                onHostPassClicked = onHostPassClicked,
                                onBarkochbaAnswered = onBarkochbaAnswered,
                                enabled = data.gameStatus == GameStatus.ONGOING
                            )
                        }
                    }
                )
            }
            AnimatedVisibility(
                visible = data.isAskQuestionButtonVisible,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                DebouncedButton(
                    onClick = onAskQuestionClicked,
                    shape = CustomRoundedCornerShape(),
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.ask_question),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionItem(
    question: QuestionItem,
    playerIsHost: Boolean,
    onPlayerAnswerClick: (String, String, Boolean) -> Unit,
    onBarkochbaAnswered: (Boolean, String) -> Unit,
    onAcceptHostAnswerClicked: (String, String) -> Unit,
    onDeclineHostAnswerClicked: (String) -> Unit,
    onHostPassClicked: (String) -> Unit,
    enabled: Boolean
) {
    Crossfade(playerIsHost to question) { (isHost, questionItem) ->
        when (questionItem) {
            is HostAnswersQuestionItem -> {
                HostAnswersQuestionItemComp(
                    questionItem,
                    onPlayerAnswerClick,
                    onHostPassClicked,
                    enabled
                )
            }

            is BarkochbaAnsweredQuestionItem -> {
                BarkochbaAnsweredQuestionItemComp(questionItem)
            }

            is CorrectAnswerQuestionItem -> {
                CorrectAnswerQuestionItemComp(questionItem)
            }

            is HostSeesBarkochbaQuestionItem -> {
                HostSeesBarkochbaQuestionItemComp(questionItem, onBarkochbaAnswered, enabled)
            }

            is HostWaitsForOwnerQuestionItem -> {
                HostWaitsForOwnerQuestionItemComp(questionItem)
            }

            is HostWaitsForPlayersQuestionItem -> {
                HostWaitsForPlayersQuestionItemComp(questionItem)
            }

            is OwnerEvaluatesQuestionItem -> {
                OwnerEvaluatesQuestionItemComp(
                    ownerEvaluatesQuestionItem = questionItem,
                    onAcceptHostAnswerClicked = onAcceptHostAnswerClicked,
                    onDeclineHostAnswerClicked = onDeclineHostAnswerClicked,
                    enabled = enabled
                )
            }

            is PlayerCanAnswerQuestionItem -> {
                PlayerCanAnswerQuestionItemComp(questionItem, onPlayerAnswerClick, enabled)
            }

            is PlayerSeesBarkochbaQuestionItem -> {
                PlayerSeesBarkochbaQuestionItemComp(questionItem)
            }

            is PlayerWaitsForHostQuestionItem -> {
                PlayerWaitsForHostQuestionItemComp(questionItem)
            }

            is PlayerWaitsForOwnerQuestionItem -> {
                PlayerWaitsForOwnerQuestionItemComp(questionItem)
            }

            is QuestionOwnerWaitsForPlayers -> {
                QuestionOwnerWaitsForPlayersComp(questionItem)
            }

            is WrongAnswerQuestionItem -> {
                WrongAnswerQuestionItemComp(questionItem, isHost)
            }

            is GuessedByHostQuestionItem -> {
                GuessedByHostQuestionItemComp(questionItem)
            }

            is PlayerWonQuestionItem -> {
                PlayerWonQuestionItemComp(questionItem)
            }
        }
    }
}

@Composable
fun HandleEvent(
    onEventHandled: () -> Unit,
    event: GameDetailsEvent?
) {
    val navController = LocalNavController.current
    when (event) {
        is GameDetailsEvent.AskQuestionClicked -> navController?.navigate(
            GameGraphRoute.ChooseQuestion.createRoute(event.gameId)
        )

        is GameDetailsEvent.NavigateToSpecifyQuestionScreen -> navController?.navigate(
            GameGraphRoute.SpecifyQuestion.createRoute(event.questionId, event.gameId)
        )

        is GameDetailsEvent.NavigateToAnswerScreen -> navController?.navigate(
            GameGraphRoute.AnswerQuestion.createRoute(
                questionId = event.questionId,
                gameId = event.gameId,
                isHost = event.isHost
            )
        )

        null -> return
    }
    onEventHandled()
}

@Composable
fun HostAnswersQuestionItemComp(
    hostAnswersQuestionItem: HostAnswersQuestionItem,
    onHostAnswerClick: (String, String, Boolean) -> Unit,
    onHostPassClicked: (String) -> Unit,
    enabled: Boolean
) {
    QuestionCard(
        question = hostAnswersQuestionItem,
        containerColor = primaryContainerLight,
        labelColor = onPrimaryContainerLight,
        labelText = stringResource(Res.string.your_up_label)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuestionActionButton(
                text = stringResource(Res.string.answer_button_label),
                contentColor = MrXTheme.extraColors.onOkSurfaceColor,
                containerColor = MrXTheme.extraColors.okSurfaceColor,
                enabled = enabled,
                onClick = {
                    onHostAnswerClick(
                        hostAnswersQuestionItem.uuid,
                        hostAnswersQuestionItem.gameId,
                        true
                    )
                }
            )
            QuestionActionButton(
                text = stringResource(Res.string.pass_button_label),
                contentColor = MrXTheme.extraColors.onWarningSurfaceColor,
                containerColor = MrXTheme.extraColors.warningSurfaceColor,
                enabled = enabled,
                onClick = { onHostPassClicked(hostAnswersQuestionItem.uuid) }
            )
        }
    }
}

@Composable
fun HostWaitsForPlayersQuestionItemComp(
    hostWaitsForPlayersQuestionItem: HostWaitsForPlayersQuestionItem
) {
    QuestionCard(
        question = hostWaitsForPlayersQuestionItem,
        containerColor = tertiaryContainerLight,
        labelColor = onTertiaryContainerLight,
        labelText = stringResource(Res.string.waiting_for_players_label)
    )
}

@Composable
fun HostWaitsForOwnerQuestionItemComp(
    hostWaitsForOwnerQuestionItem: HostWaitsForOwnerQuestionItem
) {
    QuestionCard(
        question = hostWaitsForOwnerQuestionItem,
        containerColor = tertiaryContainerLight,
        labelColor = onTertiaryContainerLight,
        labelText = stringResource(Res.string.waiting_for_owner_label)
    ) {
        AnimatedNullability(hostWaitsForOwnerQuestionItem.hostAnswer) { hostAnswer ->
            Text(
                text = stringResource(
                    Res.string.host_answer_template,
                    hostAnswer
                ), modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun CorrectAnswerQuestionItemComp(
    correctAnswerQuestionItem: CorrectAnswerQuestionItem
) {
    QuestionCard(
        question = correctAnswerQuestionItem,
        containerColor = if (correctAnswerQuestionItem.isHost)
            errorContainerLight
        else
            MrXTheme.extraColors.okSurfaceColor,
        labelColor = if (correctAnswerQuestionItem.isHost)
            onErrorContainerLight
        else
            MrXTheme.extraColors.onOkSurfaceColor,
        labelText = stringResource(Res.string.correct_answer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            AnimatedNullability(correctAnswerQuestionItem.hostAnswer) {
                Text(text = stringResource(Res.string.host_answer_template, it))
            }
            AnimatedNullability(correctAnswerQuestionItem.correctAnswer) {
                Text(text = stringResource(Res.string.correct_answer_template, it))
            }
        }
    }
}

@Composable
fun WrongAnswerQuestionItemComp(
    wrongAnswerQuestionItem: WrongAnswerQuestionItem,
    isHost: Boolean
) {
    if (isHost) {
        QuestionCard(
            question = wrongAnswerQuestionItem,
            containerColor = MrXTheme.extraColors.okSurfaceColor,
            labelColor = MrXTheme.extraColors.onOkSurfaceColor,
            labelText = stringResource(Res.string.wrong_answer_label)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedNullability(wrongAnswerQuestionItem.hostAnswer) {
                    Text(
                        text = stringResource(Res.string.host_answer_template, it),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                AnimatedNullability(wrongAnswerQuestionItem.playerAnswer) {
                    Text(
                        text = stringResource(Res.string.player_answer_template, it),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                AnimatedNullability(wrongAnswerQuestionItem.expectedAnswer) {
                    Text(
                        text = stringResource(Res.string.correct_answer_template, it),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    } else {
        QuestionCard(
            question = wrongAnswerQuestionItem,
            containerColor = MrXTheme.extraColors.warningSurfaceColor,
            labelColor = MrXTheme.extraColors.onWarningSurfaceColor,
            labelText = stringResource(Res.string.wrong_answer_label)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedNullability(wrongAnswerQuestionItem.hostAnswer) {
                    Text(
                        text = stringResource(Res.string.host_answer_template, it),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                AnimatedNullability(wrongAnswerQuestionItem.playerAnswer) {
                    Text(
                        text = stringResource(Res.string.player_answer_template, it),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                AnimatedNullability(wrongAnswerQuestionItem.expectedAnswer) {
                    Text(
                        text = stringResource(Res.string.correct_answer_template, it),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HostSeesBarkochbaQuestionItemComp(
    hostSeesBarkochbaQuestionItem: HostSeesBarkochbaQuestionItem,
    onBarkochbaAnswered: (Boolean, String) -> Unit,
    enabled: Boolean
) {
    QuestionCard(
        question = hostSeesBarkochbaQuestionItem,
        containerColor = primaryContainerLight,
        labelColor = onPrimaryContainerLight,
        labelText = stringResource(Res.string.your_up_label)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedNullability(hostSeesBarkochbaQuestionItem.playerAnswer) {
                Text(text = it, modifier = Modifier.padding(20.dp))
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(), 1.dp,
                color = onPrimaryContainerLight
            )
            Text(
                text = "${hostSeesBarkochbaQuestionItem.barkochbaText}?",
                modifier = Modifier.padding(20.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuestionActionButton(
                    text = stringResource(Res.string.true_button_label),
                    contentColor = MrXTheme.extraColors.onOkSurfaceColor,
                    containerColor = MrXTheme.extraColors.okSurfaceColor,
                    enabled = enabled,
                    onClick = { onBarkochbaAnswered(true, hostSeesBarkochbaQuestionItem.uuid) }
                )
                QuestionActionButton(
                    text = stringResource(Res.string.false_button_label),
                    contentColor = MrXTheme.extraColors.warningSurfaceColor,
                    containerColor = MrXTheme.extraColors.onWarningSurfaceColor,
                    enabled = enabled,
                    onClick = { onBarkochbaAnswered(false, hostSeesBarkochbaQuestionItem.uuid) }
                )
            }
        }
    }
}

@Composable
fun BarkochbaAnsweredQuestionItemComp(
    barkochbaAnsweredQuestionItem: BarkochbaAnsweredQuestionItem
) {
    QuestionCard(
        question = barkochbaAnsweredQuestionItem,
        containerColor = if (barkochbaAnsweredQuestionItem.barkochbaAnswer)
            MrXTheme.extraColors.okSurfaceColor
        else
            errorContainerLight,
        labelColor = if (barkochbaAnsweredQuestionItem.barkochbaAnswer)
            MrXTheme.extraColors.onOkSurfaceColor
        else
            onErrorContainerLight,
        labelText = stringResource(Res.string.correct_answer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedNullability(barkochbaAnsweredQuestionItem.hostAnswer) {
                Text(
                    text = stringResource(Res.string.host_answer_template, it),
                    modifier = Modifier.padding(12.dp)
                )
            }
            AnimatedNullability(barkochbaAnsweredQuestionItem.correctAnswer) {
                Text(
                    text = stringResource(Res.string.correct_answer_text, it),
                    modifier = Modifier.padding(12.dp)
                )
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = if (barkochbaAnsweredQuestionItem.barkochbaAnswer)
                    MrXTheme.extraColors.onOkSurfaceColor
                else
                    onErrorContainerLight
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${barkochbaAnsweredQuestionItem.barkochbaText}?")
                Text(
                    text = stringResource(
                        if (barkochbaAnsweredQuestionItem.barkochbaAnswer)
                            Res.string.yes
                        else
                            Res.string.no
                    )
                )
            }
        }
    }
}

@Composable
fun PlayerWaitsForHostQuestionItemComp(
    playerWaitsForHostQuestionItem: PlayerWaitsForHostQuestionItem
) {
    QuestionCard(
        question = playerWaitsForHostQuestionItem,
        containerColor = tertiaryContainerLight,
        labelColor = onTertiaryContainerLight,
        labelText = stringResource(Res.string.waiting_for_host_label)
    )
}

@Composable
fun PlayerCanAnswerQuestionItemComp(
    playerCanAnswerQuestionItem: PlayerCanAnswerQuestionItem,
    onClick: (String, String, Boolean) -> Unit,
    enabled: Boolean
) {
    QuestionCard(
        question = playerCanAnswerQuestionItem,
        containerColor = primaryContainerLight,
        labelColor = onPrimaryContainerLight,
        labelText = stringResource(Res.string.your_up_label)
    ) {
        QuestionActionButton(
            text = stringResource(Res.string.answer_button_label),
            contentColor = onPrimaryContainerLight,
            containerColor = primaryContainerLight,
            enabled = enabled,
            onClick = {
                onClick(
                    playerCanAnswerQuestionItem.uuid,
                    playerCanAnswerQuestionItem.gameId,
                    false
                )
            }
        )
    }
}

@Composable
fun PlayerWaitsForOwnerQuestionItemComp(
    playerWaitsForOwnerQuestionItem: PlayerWaitsForOwnerQuestionItem
) {
    QuestionCard(
        question = playerWaitsForOwnerQuestionItem,
        containerColor = tertiaryContainerLight,
        labelColor = onTertiaryContainerLight,
        labelText = stringResource(Res.string.waiting_for_owner_label)
    ) {
        AnimatedNullability(playerWaitsForOwnerQuestionItem.hostAnswer) {
            Text(
                text = stringResource(Res.string.host_answer_template, it),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun OwnerEvaluatesQuestionItemComp(
    ownerEvaluatesQuestionItem: OwnerEvaluatesQuestionItem,
    onAcceptHostAnswerClicked: (String, String) -> Unit,
    onDeclineHostAnswerClicked: (String) -> Unit,
    enabled: Boolean
) {
    QuestionCard(
        question = ownerEvaluatesQuestionItem,
        containerColor = primaryContainerLight,
        labelText = stringResource(Res.string.your_up_label),
        labelColor = onPrimaryContainerLight
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedNullability(ownerEvaluatesQuestionItem.hostAnswer) {
                Text(text = stringResource(Res.string.host_answer_template, it))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuestionActionButton(
                    text = stringResource(Res.string.accept_button_label),
                    contentColor = MrXTheme.extraColors.onOkSurfaceColor,
                    containerColor = MrXTheme.extraColors.okSurfaceColor,
                    onClick = {
                        onAcceptHostAnswerClicked(
                            ownerEvaluatesQuestionItem.uuid,
                            ownerEvaluatesQuestionItem.gameId
                        )
                    },
                    enabled = enabled
                )
                QuestionActionButton(
                    text = stringResource(Res.string.decline_button_label),
                    contentColor = onErrorContainerLight,
                    containerColor = errorContainer,
                    onClick = { onDeclineHostAnswerClicked(ownerEvaluatesQuestionItem.uuid) },
                    enabled = enabled
                )
            }
        }
    }
}

@Composable
fun QuestionOwnerWaitsForPlayersComp(
    questionOwnerWaitsForPlayers: QuestionOwnerWaitsForPlayers
) {
    QuestionCard(
        question = questionOwnerWaitsForPlayers,
        containerColor = tertiaryContainerLight,
        labelColor = onTertiaryContainerLight,
        labelText = stringResource(Res.string.waiting_label)
    ) {
        Text(
            text = stringResource(Res.string.waiting_for_players_to_answer),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun PlayerSeesBarkochbaQuestionItemComp(
    playerSeesBarkochbaQuestionItem: PlayerSeesBarkochbaQuestionItem
) {
    QuestionCard(
        question = playerSeesBarkochbaQuestionItem,
        containerColor = tertiaryContainerLight,
        labelColor = onTertiaryContainerLight,
        labelText = stringResource(Res.string.waiting_for_host_label)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedNullability(playerSeesBarkochbaQuestionItem.playerAnswer) {
                Text(text = it, modifier = Modifier.padding(20.dp))
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(), 1.dp,
                color = onTertiaryContainerLight
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${playerSeesBarkochbaQuestionItem.barkochbaText}?")
                Text(
                    text = stringResource(Res.string.yes_or_no_text),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun GuessedByHostQuestionItemComp(guessedByHostQuestionItem: GuessedByHostQuestionItem) {
    QuestionCard(
        question = guessedByHostQuestionItem,
        containerColor = if (guessedByHostQuestionItem.isHost)
            MrXTheme.extraColors.okSurfaceColor
        else
            errorContainerLight,
        labelColor = if (guessedByHostQuestionItem.isHost)
            MrXTheme.extraColors.onOkSurfaceColor
        else
            onErrorContainerLight,
        labelText = stringResource(
            if (guessedByHostQuestionItem.isHost)
                Res.string.host_guessed_for_host_label
            else
                Res.string.host_guessed_label
        )
    ) {
        AnimatedNullability(guessedByHostQuestionItem.hostAnswer) {
            Text(
                text = stringResource(Res.string.host_answer_template, it),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun PlayerWonQuestionItemComp(questionItem: PlayerWonQuestionItem) {
    QuestionCard(
        question = questionItem,
        containerColor = MrXTheme.extraColors.okSurfaceColor,
        labelColor = MrXTheme.extraColors.onOkSurfaceColor,
        labelText = stringResource(Res.string.game_over)
    ) {
        Text(
            text = stringResource(Res.string.correct_answer_template, questionItem.answer),
            modifier = Modifier.padding(12.dp)
        )
    }
}


@Composable
fun QuestionCard(
    question: QuestionItem,
    containerColor: Color,
    labelColor: Color? = null,
    labelText: String = "",
    content: @Composable () -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            labelColor?.let { color ->
                CardLabel(
                    text = labelText,
                    color = color,
                    modifier = Modifier.align(Alignment.End)
                )
            }
            Text(
                text = "${question.text}?",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(16.dp)
            )
            content()
        }
    }
}

@Composable
fun QuestionActionButton(
    text: String,
    contentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier.padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            shape = RoundedCornerShape(10.dp),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors().copy(
                contentColor = contentColor,
                containerColor = containerColor
            ),
            onClick = { onClick() }
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun CardLabel(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .border(
                width = 2.dp,
                color = color,
                shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp)
            )
            .padding(8.dp)
    )
}

private const val PLACEMENT_DURATION = 600
private const val ENTER_TRANSITION_DURATION = 700
private const val EXIT_TRANSITION_DURATION = 700
