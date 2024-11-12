package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.button.PrimaryCardButton
import com.palkesz.mr.x.core.ui.components.text.buildCorrectAnswer
import com.palkesz.mr.x.core.ui.components.text.buildCorrectHostAnswer
import com.palkesz.mr.x.core.ui.components.text.buildCorrectPlayerAnswer
import com.palkesz.mr.x.core.ui.components.text.buildWrongHostAnswer
import com.palkesz.mr.x.core.ui.components.text.buildWrongPlayerAnswer
import com.palkesz.mr.x.feature.games.game.QuestionItem
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.accept_button_label
import mrx.composeapp.generated.resources.answer_button_label
import mrx.composeapp.generated.resources.decline_button_label
import mrx.composeapp.generated.resources.ic_crisis_alert
import mrx.composeapp.generated.resources.ic_hourglass_bottom
import mrx.composeapp.generated.resources.ic_hourglass_top
import mrx.composeapp.generated.resources.ic_play_circle
import mrx.composeapp.generated.resources.ic_published_with_changes
import mrx.composeapp.generated.resources.ic_shield_error
import mrx.composeapp.generated.resources.ic_star
import mrx.composeapp.generated.resources.ic_trophy
import mrx.composeapp.generated.resources.pass_button_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun QuestionItemCard(
    modifier: Modifier = Modifier,
    item: QuestionItem,
    isGameOngoing: Boolean,
    onGuessAsHostClicked: (String) -> Unit,
    onPassAsHostClicked: (String) -> Unit,
    onGuessAsPlayerClicked: (String) -> Unit,
    onAcceptAsOwnerClicked: (String) -> Unit,
    onDeclineAsOwnerClicked: (String) -> Unit,
) {
    when (item) {
        is QuestionItem.WaitingForHostItem -> {
            WaitingForHostQuestionCard(modifier = modifier, item = item)
        }

        is QuestionItem.GuessAsHostItem -> {
            GuessAsHostQuestionCard(
                modifier = modifier,
                item = item,
                isGameOngoing = isGameOngoing,
                onAnswerClicked = onGuessAsHostClicked,
                onPassClicked = onPassAsHostClicked,
            )
        }

        is QuestionItem.GuessedByHostItem -> {
            GuessedByHostQuestionCard(modifier = modifier, item = item)
        }

        is QuestionItem.PlayersWonItem -> {
            PlayersWonQuestionCard(modifier = modifier, item = item)
        }

        is QuestionItem.GuessAsPlayerItem -> {
            GuessAsPlayerQuestionCard(
                modifier = modifier,
                item = item,
                isGameOngoing = isGameOngoing,
                onAnswerClicked = onGuessAsPlayerClicked,
            )
        }

        is QuestionItem.MissedByPlayerItem -> {
            MissedByPlayerQuestionCard(modifier = modifier, item = item)
        }

        is QuestionItem.VerifyAsOwnerItem -> {
            VerifyAsOwnerQuestionCard(
                modifier = modifier,
                item = item,
                isGameOngoing = isGameOngoing,
                onAcceptClicked = onAcceptAsOwnerClicked,
                onDeclineClicked = onDeclineAsOwnerClicked,
            )
        }

        is QuestionItem.WaitingForOwnerItem -> {
            WaitingForOwnerQuestionCard(modifier = Modifier, item = item)
        }

        is QuestionItem.WaitingForPlayersItem -> {
            WaitingForPlayersQuestionCard(modifier = modifier, item = item)
        }

        is QuestionItem.GuessedByPlayerItem -> {
            GuessedByPlayerQuestionCard(modifier = modifier, item = item)
        }
    }
}

@Composable
private fun WaitingForHostQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.WaitingForHostItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_hourglass_top),
    )
}

@Composable
private fun GuessAsHostQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.GuessAsHostItem,
    isGameOngoing: Boolean,
    onAnswerClicked: (String) -> Unit,
    onPassClicked: (String) -> Unit,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_play_circle),
    ) {
        QuestionCardButtons(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp),
            enabled = isGameOngoing,
            id = item.id,
            first = stringResource(Res.string.answer_button_label),
            second = stringResource(Res.string.pass_button_label),
            onFirstClicked = onAnswerClicked,
            onSecondClicked = onPassClicked,
        )
    }
}

@Composable
private fun WaitingForPlayersQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.WaitingForPlayersItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_hourglass_bottom),
    ) {
        AnimatedNullability(item.hostAnswer) { answer ->
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                text = buildWrongHostAnswer(answer = answer),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun GuessAsPlayerQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.GuessAsPlayerItem,
    isGameOngoing: Boolean,
    onAnswerClicked: (String) -> Unit,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_play_circle),
    ) {
        AnimatedNullability(item.hostAnswer) { answer ->
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                text = buildWrongHostAnswer(answer = answer),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        PrimaryCardButton(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = stringResource(Res.string.answer_button_label),
            enabled = isGameOngoing,
            onClick = { onAnswerClicked(item.id) }
        )
    }
}

@Composable
private fun WaitingForOwnerQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.WaitingForOwnerItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_published_with_changes),
    ) {
        AnimatedNullability(item.hostAnswer) { answer ->
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                text = buildWrongHostAnswer(answer = answer),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
            )
        }
    }
}

@Composable
private fun VerifyAsOwnerQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.VerifyAsOwnerItem,
    isGameOngoing: Boolean,
    onAcceptClicked: (String) -> Unit,
    onDeclineClicked: (String) -> Unit,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_published_with_changes),
    ) {
        AnimatedNullability(item.hostAnswer) { answer ->
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                text = buildWrongHostAnswer(answer = answer),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
            )
        }
        QuestionCardButtons(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            enabled = isGameOngoing,
            id = item.id,
            first = stringResource(Res.string.accept_button_label),
            second = stringResource(Res.string.decline_button_label),
            onFirstClicked = onAcceptClicked,
            onSecondClicked = onDeclineClicked,
        )
    }
}

@Composable
private fun GuessedByPlayerQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.GuessedByPlayerItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_star),
    ) {
        AnimatedNullability(item.hostAnswer) { answer ->
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                text = buildWrongHostAnswer(answer = answer),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = buildCorrectPlayerAnswer(item.answer, item.owner),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun MissedByPlayerQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.MissedByPlayerItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_crisis_alert),
    ) {
        AnimatedNullability(item.hostAnswer) { answer ->
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                text = buildWrongHostAnswer(answer = answer),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = buildWrongPlayerAnswer(item.answer, item.owner),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun GuessedByHostQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.GuessedByHostItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_shield_error),
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
            text = buildCorrectHostAnswer(answer = item.hostAnswer),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun PlayersWonQuestionCard(
    modifier: Modifier = Modifier,
    item: QuestionItem.PlayersWonItem,
) {
    QuestionCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_trophy),
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
            text = buildCorrectAnswer(
                answer = item.answer,
                highlightColor = MaterialTheme.colorScheme.primary,
            ),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
