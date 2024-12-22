package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.button.PrimaryCardButton
import com.palkesz.mr.x.feature.games.game.QuestionItem
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.accept_button_label
import mrx.composeapp.generated.resources.answer_button_label
import mrx.composeapp.generated.resources.decline_button_label
import mrx.composeapp.generated.resources.ic_correct_answer
import mrx.composeapp.generated.resources.ic_crisis_alert
import mrx.composeapp.generated.resources.ic_hourglass_bottom
import mrx.composeapp.generated.resources.ic_hourglass_top
import mrx.composeapp.generated.resources.ic_play_circle
import mrx.composeapp.generated.resources.ic_published_with_changes
import mrx.composeapp.generated.resources.ic_shield_error
import mrx.composeapp.generated.resources.ic_star
import mrx.composeapp.generated.resources.ic_trophy
import mrx.composeapp.generated.resources.ic_wrong_answer
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
            WaitingForOwnerQuestionCard(modifier = modifier, item = item)
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_hourglass_top),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
    }
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_play_circle),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        QuestionCardButtons(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 16.dp, end = 16.dp),
            id = item.id,
            enabled = isGameOngoing,
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_hourglass_bottom),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnimatedNullability(item.hostAnswer) { answer ->
            AnswerText(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = answer,
                owner = item.hostName,
                isHost = true,
                icon = vectorResource(Res.drawable.ic_wrong_answer),
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_play_circle),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnimatedNullability(item.hostAnswer) { answer ->
            AnswerText(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = answer,
                owner = item.hostName,
                isHost = true,
                icon = vectorResource(Res.drawable.ic_wrong_answer),
            )
        }
        PrimaryCardButton(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_published_with_changes),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnimatedNullability(item.hostAnswer) { answer ->
            AnswerText(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = answer,
                owner = item.hostName,
                isHost = true,
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_play_circle),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnimatedNullability(item.hostAnswer) { answer ->
            AnswerText(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = answer,
                owner = item.hostName,
                isHost = true,
            )
        }
        QuestionCardButtons(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            id = item.id,
            enabled = isGameOngoing,
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_star),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnimatedNullability(item.hostAnswer) { answer ->
            AnswerText(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = answer,
                owner = item.hostName,
                isHost = true,
                icon = vectorResource(Res.drawable.ic_wrong_answer),
            )
        }
        AnswerText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = item.answer,
            owner = item.answerOwner,
            isHost = false,
            icon = vectorResource(Res.drawable.ic_correct_answer),
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_crisis_alert),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnimatedNullability(item.hostAnswer) { answer ->
            AnswerText(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = answer,
                owner = item.hostName,
                isHost = true,
                icon = vectorResource(Res.drawable.ic_wrong_answer),
            )
        }
        AnswerText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = item.answer,
            owner = item.answerOwner,
            isHost = false,
            icon = vectorResource(Res.drawable.ic_wrong_answer),
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_shield_error),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnswerText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = item.hostAnswer,
            owner = item.hostName,
            isHost = true,
            icon = vectorResource(Res.drawable.ic_correct_answer),
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
        number = item.number,
        icon = vectorResource(Res.drawable.ic_trophy),
    ) {
        QuestionText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.text,
            owner = item.owner,
        )
        AnswerText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = item.answer,
            owner = item.owner,
            isHost = false,
            icon = vectorResource(Res.drawable.ic_correct_answer),
        )
    }
}
