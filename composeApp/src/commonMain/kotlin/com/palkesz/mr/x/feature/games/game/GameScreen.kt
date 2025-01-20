package com.palkesz.mr.x.feature.games.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.animation.AnimatedLazyColumn
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.ui.modifiers.conditional
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.getOrNull
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import com.palkesz.mr.x.feature.games.GameGraph
import com.palkesz.mr.x.feature.games.game.ui.BarkochbaCard
import com.palkesz.mr.x.feature.games.game.ui.QuestionItemCard
import com.palkesz.mr.x.feature.games.game.ui.QuestionPager
import kotlinx.collections.immutable.ImmutableList
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_barkochba_question_button_label
import mrx.composeapp.generated.resources.ask_question_button_label
import mrx.composeapp.generated.resources.no_barkochba_questions_host_message
import mrx.composeapp.generated.resources.no_barkochba_questions_message
import mrx.composeapp.generated.resources.no_questions_host_message
import mrx.composeapp.generated.resources.no_questions_message
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    GameScreenContent(
        viewState = viewState,
        onAskQuestionClicked = viewModel::onAskQuestionClicked,
        onGuessAsPlayerClicked = viewModel::onGuessAsPlayerClicked,
        onPassAsHostClicked = viewModel::onPassAsHostClicked,
        onGuessAsHostClicked = viewModel::onGuessAsHostClicked,
        onQrCodeClicked = viewModel::onQrCodeClicked,
        onAcceptAsOwnerClicked = viewModel::onAcceptAsOwnerClicked,
        onDeclineAsOwnerClicked = viewModel::onDeclineAsOwnerClicked,
        onAskBarkochbaQuestionClicked = viewModel::onAskBarkochbaQuestionClicked,
        onBarkochbaQuestionAnswered = viewModel::onBarkochbaQuestionAnswered,
        onPageSelected = viewModel::onPageSelected,
        onLeavingScreen = viewModel::onLeavingScreen,
        onRetry = viewModel::onRetry,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun GameScreenContent(
    viewState: ViewState<GameViewState>,
    onGuessAsHostClicked: (String) -> Unit,
    onPassAsHostClicked: (String) -> Unit,
    onGuessAsPlayerClicked: (String) -> Unit,
    onAcceptAsOwnerClicked: (String) -> Unit,
    onDeclineAsOwnerClicked: (String) -> Unit,
    onAskQuestionClicked: () -> Unit,
    onAskBarkochbaQuestionClicked: () -> Unit,
    onBarkochbaQuestionAnswered: (String, Boolean) -> Unit,
    onQrCodeClicked: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onLeavingScreen: () -> Unit,
    onEventHandled: () -> Unit,
    onRetry: () -> Unit,
) {
    with(viewState.getOrNull()) {
        TitleBarEffect(
            details = TitleBarDetails.GameTitleBarDetails(
                firstName = this?.firstName,
                lastName = this?.lastName,
                hostName = this?.host,
                isHost = this?.isHost ?: false,
                onQrCodeClicked = onQrCodeClicked,
            )
        )
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            onLeavingScreen()
        }
    }
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { state ->
        val pagerState = rememberPagerState { 2 }
        HandleEvent(
            onEventHandled = onEventHandled,
            goToTab = { pagerState.scrollToPage(page = it) },
            event = state.event
        )
        QuestionPager(
            pagerState = pagerState,
            onPageSelected = onPageSelected,
            questionBadgeCount = state.questionBadgeCount,
            barkochbaBadgeCount = state.barkochbaBadgeCount,
            normalPage = {
                NormalQuestionPage(
                    modifier = Modifier.fillMaxSize(),
                    questions = state.questions,
                    animatedQuestionId = state.animatedQuestionId,
                    isGameOngoing = state.isGameOngoing,
                    isHost = state.isHost,
                    isAskQuestionButtonVisible = state.isAskQuestionButtonVisible,
                    onPassAsHostClicked = onPassAsHostClicked,
                    onGuessAsHostClicked = onGuessAsHostClicked,
                    onAcceptAsOwnerClicked = onAcceptAsOwnerClicked,
                    onDeclineAsOwnerClicked = onDeclineAsOwnerClicked,
                    onGuessAsPlayerClicked = onGuessAsPlayerClicked,
                    onAskQuestionClicked = onAskQuestionClicked,
                )
            },
            barkochbaPage = {
                BarkochbaQuestionPage(
                    modifier = Modifier.fillMaxSize(),
                    questions = state.barkochbaQuestions,
                    animatedQuestionId = state.animatedBarkochbaQuestionId,
                    isGameOngoing = state.isGameOngoing,
                    isHost = state.isHost,
                    isAskBarkochbaQuestionButtonVisible = state.isAskBarkochbaQuestionButtonVisible,
                    onAskQuestionClicked = onAskBarkochbaQuestionClicked,
                    onBarkochbaQuestionAnswered = onBarkochbaQuestionAnswered,
                )
            }
        )
    }
}

@Composable
private fun NormalQuestionPage(
    modifier: Modifier = Modifier,
    questions: ImmutableList<QuestionItem>,
    animatedQuestionId: String?,
    isGameOngoing: Boolean,
    isHost: Boolean,
    isAskQuestionButtonVisible: Boolean,
    onGuessAsHostClicked: (String) -> Unit,
    onPassAsHostClicked: (String) -> Unit,
    onGuessAsPlayerClicked: (String) -> Unit,
    onAcceptAsOwnerClicked: (String) -> Unit,
    onDeclineAsOwnerClicked: (String) -> Unit,
    onAskQuestionClicked: () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CrossFade(
            modifier = Modifier.fillMaxWidth().weight(1f),
            condition = questions.isEmpty(),
            onConditionTrue = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(if (isHost) Res.string.no_questions_host_message else Res.string.no_questions_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            onConditionFalse = {
                AnimatedLazyColumn(
                    modifier = Modifier.padding(top = 16.dp),
                    items = questions,
                    animatedItemKey = animatedQuestionId,
                    getKey = { id },
                ) { index, item ->
                    QuestionItemCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .conditional(condition = questions.lastIndex != index) {
                                padding(bottom = 16.dp)
                            },
                        item = item,
                        isGameOngoing = isGameOngoing,
                        onPassAsHostClicked = onPassAsHostClicked,
                        onGuessAsHostClicked = onGuessAsHostClicked,
                        onAcceptAsOwnerClicked = onAcceptAsOwnerClicked,
                        onDeclineAsOwnerClicked = onDeclineAsOwnerClicked,
                        onGuessAsPlayerClicked = onGuessAsPlayerClicked,
                    )
                }
            }
        )
        AnimatedVisibility(visible = isAskQuestionButtonVisible) {
            PrimaryButton(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                text = stringResource(Res.string.ask_question_button_label),
                onClick = onAskQuestionClicked,
            )
        }
    }
}

@Composable
private fun BarkochbaQuestionPage(
    modifier: Modifier = Modifier,
    questions: ImmutableList<BarkochbaItem>,
    animatedQuestionId: String?,
    isGameOngoing: Boolean,
    isHost: Boolean,
    isAskBarkochbaQuestionButtonVisible: Boolean,
    onBarkochbaQuestionAnswered: (String, Boolean) -> Unit,
    onAskQuestionClicked: () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CrossFade(
            modifier = Modifier.fillMaxWidth().weight(1f),
            condition = questions.isEmpty(),
            onConditionTrue = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(if (isHost) Res.string.no_barkochba_questions_host_message else Res.string.no_barkochba_questions_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            onConditionFalse = {
                AnimatedLazyColumn(
                    modifier = Modifier.padding(top = 16.dp),
                    items = questions,
                    animatedItemKey = animatedQuestionId,
                    getKey = { id },
                ) { index, item ->
                    BarkochbaCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .conditional(condition = questions.lastIndex != index) {
                                padding(bottom = 16.dp)
                            },
                        item = item,
                        isGameOngoing = isGameOngoing,
                        onYesClicked = { onBarkochbaQuestionAnswered(it, true) },
                        onNoClicked = { onBarkochbaQuestionAnswered(it, false) }
                    )
                }
            }
        )
        AnimatedVisibility(visible = isAskBarkochbaQuestionButtonVisible) {
            PrimaryButton(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                text = stringResource(Res.string.ask_barkochba_question_button_label),
                onClick = onAskQuestionClicked,
            )
        }
    }
}

@Composable
private fun HandleEvent(
    event: GameEvent?,
    goToTab: suspend (Int) -> Unit,
    onEventHandled: () -> Unit
) {
    HandleEventEffect(key1 = event) { gameEvent, _, _, navController ->
        when (gameEvent) {
            is GameEvent.GoToTab -> {
                goToTab(gameEvent.index)
            }

            is GameEvent.NavigateToQrCode -> {
                navController?.navigate(GameGraph.QrCode(id = gameEvent.gameId))
            }

            is GameEvent.NavigateToCreateQuestion -> {
                navController?.navigate(GameGraph.CreateQuestion(gameId = gameEvent.gameId))
            }

            is GameEvent.NavigateToGuessQuestion -> {
                navController?.navigate(
                    GameGraph.GuessQuestion(
                        gameId = gameEvent.gameId,
                        questionId = gameEvent.questionId,
                    )
                )
            }

            is GameEvent.NavigateToSpecifyQuestion -> {
                navController?.navigate(
                    GameGraph.SpecifyQuestion(
                        gameId = gameEvent.gameId,
                        questionId = gameEvent.questionId,
                    )
                )
            }

            is GameEvent.NavigateToCreateBarkochbaQuestion -> {
                navController?.navigate(GameGraph.CreateBarkochbaQuestion(gameId = gameEvent.gameId))
            }
        }
        onEventHandled()
    }
}
