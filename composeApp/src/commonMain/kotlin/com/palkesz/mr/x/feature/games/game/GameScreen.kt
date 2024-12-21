package com.palkesz.mr.x.feature.games.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.modifiers.conditional
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.games.GameGraph
import com.palkesz.mr.x.feature.games.game.ui.BarkochbaCard
import com.palkesz.mr.x.feature.games.game.ui.GameTitleBar
import com.palkesz.mr.x.feature.games.game.ui.QuestionItemCard
import com.palkesz.mr.x.feature.games.game.ui.QuestionPager
import kotlinx.collections.immutable.ImmutableList
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_barkochba_question_button_label
import mrx.composeapp.generated.resources.ask_question_button_label
import mrx.composeapp.generated.resources.no_barkochba_questions_message
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
    onEventHandled: () -> Unit,
    onRetry: () -> Unit,
) {
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { state ->
        HandleEvent(onEventHandled = onEventHandled, event = state.event)
        Column {
            GameTitleBar(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                firstName = state.firstName,
                lastName = state.lastName,
                hostName = state.host,
                isHost = state.isHost,
                onQrCodeClicked = onQrCodeClicked,
            )
            QuestionPager(
                modifier = Modifier.padding(all = 16.dp),
                normalPage = {
                    NormalQuestionPage(
                        modifier = Modifier.fillMaxSize(),
                        questions = state.questions,
                        isGameOngoing = state.isGameOngoing,
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
                        isGameOngoing = state.isGameOngoing,
                        isAskBarkochbaQuestionButtonVisible = state.isAskBarkochbaQuestionButtonVisible,
                        onAskQuestionClicked = onAskBarkochbaQuestionClicked,
                        onBarkochbaQuestionAnswered = onBarkochbaQuestionAnswered,
                    )
                }
            )
        }
    }
}

@Composable
private fun NormalQuestionPage(
    modifier: Modifier = Modifier,
    questions: ImmutableList<QuestionItem>,
    isGameOngoing: Boolean,
    isAskQuestionButtonVisible: Boolean,
    onGuessAsHostClicked: (String) -> Unit,
    onPassAsHostClicked: (String) -> Unit,
    onGuessAsPlayerClicked: (String) -> Unit,
    onAcceptAsOwnerClicked: (String) -> Unit,
    onDeclineAsOwnerClicked: (String) -> Unit,
    onAskQuestionClicked: () -> Unit,
) {
    Column(modifier = modifier) {
        CrossFade(
            modifier = Modifier.fillMaxWidth().weight(1f),
            condition = questions.isEmpty(),
            onConditionTrue = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.no_questions_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            onConditionFalse = {
                LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                    itemsIndexed(questions) { index, item ->
                        QuestionItemCard(
                            modifier = Modifier
                                .fillMaxWidth()
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
            }
        )
        AnimatedVisibility(visible = isAskQuestionButtonVisible) {
            PrimaryButton(
                modifier = Modifier.padding(top = 16.dp),
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
    isGameOngoing: Boolean,
    isAskBarkochbaQuestionButtonVisible: Boolean,
    onBarkochbaQuestionAnswered: (String, Boolean) -> Unit,
    onAskQuestionClicked: () -> Unit,
) {
    Column(modifier = modifier) {
        CrossFade(
            modifier = Modifier.fillMaxWidth().weight(1f),
            condition = questions.isEmpty(),
            onConditionTrue = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.no_barkochba_questions_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            onConditionFalse = {
                LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                    itemsIndexed(questions) { index, item ->
                        BarkochbaCard(
                            modifier = Modifier
                                .fillMaxWidth()
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
            }
        )
        AnimatedVisibility(visible = isAskBarkochbaQuestionButtonVisible) {
            PrimaryButton(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(Res.string.ask_barkochba_question_button_label),
                onClick = onAskQuestionClicked,
            )
        }
    }
}

@Composable
private fun HandleEvent(event: GameEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(key1 = event) { gameEvent, _, _, navController ->
        when (gameEvent) {
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
