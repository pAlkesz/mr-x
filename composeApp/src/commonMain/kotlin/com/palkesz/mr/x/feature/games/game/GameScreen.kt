package com.palkesz.mr.x.feature.games.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.games.game.ui.GameTitleBar
import com.palkesz.mr.x.feature.games.game.ui.QuestionItemCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_question_button_label
import mrx.composeapp.generated.resources.barkochba_question_tab_title
import mrx.composeapp.generated.resources.no_questions_message
import mrx.composeapp.generated.resources.normal_question_tab_title
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
    onQrCodeClicked: () -> Unit,
    onEventHandled: () -> Unit,
    onRetry: () -> Unit,
) {
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { state ->
        HandleEvent(onEventHandled = onEventHandled, event = state.event)
        Column {
            GameTitleBar(
                firstName = state.firstName,
                lastName = state.lastName,
                host = state.host,
                isIconVisible = state.isHost,
                onQrCodeClicked = onQrCodeClicked,
            )
            QuestionPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
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

                }
            )
        }
    }
}

@Composable
private fun QuestionPager(
    modifier: Modifier = Modifier,
    normalPage: @Composable () -> Unit,
    barkochbaPage: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        val pagerState = rememberPagerState { 2 }
        val coroutineScope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pagerState.currentPage) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(page = 0) }
                },
                text = { Text(text = stringResource(Res.string.normal_question_tab_title)) },
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(page = 1) }
                },
                text = { Text(text = stringResource(Res.string.barkochba_question_tab_title)) },
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) { index ->
            if (index == 0) normalPage() else barkochbaPage()
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
                LazyColumn {
                    items(questions) { item ->
                        QuestionItemCard(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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
                text = stringResource(Res.string.ask_question_button_label),
                onClick = onAskQuestionClicked,
            )
        }
    }
}

@Composable
private fun HandleEvent(event: GameEvent?, onEventHandled: () -> Unit) {
    //TODO
}
