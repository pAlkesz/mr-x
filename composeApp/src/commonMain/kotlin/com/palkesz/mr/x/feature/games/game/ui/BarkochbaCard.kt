package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.feature.games.game.BarkochbaItem
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_check
import mrx.composeapp.generated.resources.ic_check_circle
import mrx.composeapp.generated.resources.ic_close
import mrx.composeapp.generated.resources.ic_hourglass_top
import mrx.composeapp.generated.resources.ic_play_circle
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
        text = item.text,
        owner = item.owner,
        icon = vectorResource(Res.drawable.ic_hourglass_top),
    )
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
        text = item.text,
        owner = item.owner,
        icon = vectorResource(Res.drawable.ic_play_circle),
    ) {
        QuestionCardButtons(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp),
            enabled = isGameOngoing,
            id = item.id,
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
        text = item.text,
        owner = item.owner,
        icon = vectorResource(Res.drawable.ic_check_circle),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 16.dp),
                text = stringResource(
                    if (item.answer) {
                        Res.string.yes_answer_label
                    } else {
                        Res.string.no_answer_label
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            val iconColor by animateColorAsState(
                if (item.answer) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            Icon(
                imageVector = vectorResource(
                    if (item.answer) {
                        Res.drawable.ic_check
                    } else {
                        Res.drawable.ic_close
                    }
                ),
                tint = iconColor,
                contentDescription = null,
            )
        }
    }
}
