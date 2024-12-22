package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.feature.games.game.BarkochbaItem
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_check_circle
import mrx.composeapp.generated.resources.ic_correct_answer
import mrx.composeapp.generated.resources.ic_hourglass_top
import mrx.composeapp.generated.resources.ic_play_circle
import mrx.composeapp.generated.resources.ic_wrong_answer
import mrx.composeapp.generated.resources.no_answer_label
import mrx.composeapp.generated.resources.no_button_label
import mrx.composeapp.generated.resources.yes_answer_label
import mrx.composeapp.generated.resources.yes_button_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BarkochbaCard(
    modifier: Modifier = Modifier,
    item: BarkochbaItem,
    isGameOngoing: Boolean,
    onYesClicked: (String) -> Unit,
    onNoClicked: (String) -> Unit,
) {
    when (item) {
        is BarkochbaItem.WaitingForHostItem -> {
            WaitingForHostBarkochbaCard(modifier = modifier, item = item)
        }

        is BarkochbaItem.AnswerAsHostItem -> {
            AnswerAsHostBarkochbaCard(
                modifier = modifier,
                item = item,
                isGameOngoing = isGameOngoing,
                onYesClicked = onYesClicked,
                onNoClicked = onNoClicked,
            )
        }

        is BarkochbaItem.AnsweredByHostItem -> {
            AnsweredByHostBarkochbaCard(modifier = modifier, item = item)
        }
    }
}

@Composable
private fun WaitingForHostBarkochbaCard(
    modifier: Modifier = Modifier,
    item: BarkochbaItem.WaitingForHostItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        number = item.number,
        icon = vectorResource(Res.drawable.ic_hourglass_top),
    ) {
        QuestionText(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            text = item.text,
            owner = item.owner,
        )
    }
}

@Composable
private fun AnswerAsHostBarkochbaCard(
    modifier: Modifier = Modifier,
    item: BarkochbaItem.AnswerAsHostItem,
    isGameOngoing: Boolean,
    onYesClicked: (String) -> Unit,
    onNoClicked: (String) -> Unit,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        number = item.number,
        icon = vectorResource(Res.drawable.ic_play_circle),
    ) {
        QuestionText(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        QuestionCardButtons(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 16.dp, end = 16.dp),
            id = item.id,
            enabled = isGameOngoing,
            first = stringResource(Res.string.yes_button_label),
            second = stringResource(Res.string.no_button_label),
            onFirstClicked = onYesClicked,
            onSecondClicked = onNoClicked,
        )
    }
}

@Composable
private fun AnsweredByHostBarkochbaCard(
    modifier: Modifier = Modifier,
    item: BarkochbaItem.AnsweredByHostItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        number = item.number,
        icon = vectorResource(Res.drawable.ic_check_circle),
    ) {
        QuestionText(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnswerText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = stringResource(if (item.answer) Res.string.yes_answer_label else Res.string.no_answer_label),
            owner = item.hostName,
            isHost = true,
            icon = vectorResource(if (item.answer) Res.drawable.ic_correct_answer else Res.drawable.ic_wrong_answer),
        )
    }
}
