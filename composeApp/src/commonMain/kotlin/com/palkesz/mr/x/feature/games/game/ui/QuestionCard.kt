package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.button.PrimaryCardButton
import com.palkesz.mr.x.core.ui.components.button.SecondaryCardButton
import com.palkesz.mr.x.feature.games.game.QuestionItem
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.accept_button_label
import mrx.composeapp.generated.resources.answer_button_label
import mrx.composeapp.generated.resources.decline_button_label
import mrx.composeapp.generated.resources.host_answer_label
import mrx.composeapp.generated.resources.ic_crisis_alert
import mrx.composeapp.generated.resources.ic_hourglass_bottom
import mrx.composeapp.generated.resources.ic_hourglass_top
import mrx.composeapp.generated.resources.ic_play_circle
import mrx.composeapp.generated.resources.ic_published_with_changes
import mrx.composeapp.generated.resources.ic_shield_error
import mrx.composeapp.generated.resources.ic_star
import mrx.composeapp.generated.resources.ic_trophy
import mrx.composeapp.generated.resources.pass_button_label
import mrx.composeapp.generated.resources.player_answer_label
import mrx.composeapp.generated.resources.question_number_title
import mrx.composeapp.generated.resources.question_text
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
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                textDecoration = TextDecoration.LineThrough,
                text = stringResource(Res.string.host_answer_label, answer),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
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
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                textDecoration = TextDecoration.LineThrough,
                text = stringResource(Res.string.host_answer_label, answer),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
            )
        }
        PrimaryCardButton(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                text = stringResource(Res.string.host_answer_label, answer),
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
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                text = stringResource(Res.string.host_answer_label, answer),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
            )
        }
        QuestionCardButtons(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                textDecoration = TextDecoration.LineThrough,
                text = stringResource(Res.string.host_answer_label, answer),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = stringResource(Res.string.player_answer_label, item.answer, item.owner),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
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
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                textDecoration = TextDecoration.LineThrough,
                text = stringResource(Res.string.host_answer_label, answer),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            textDecoration = TextDecoration.LineThrough,
            text = stringResource(Res.string.player_answer_label, item.answer, item.owner),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
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
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = stringResource(Res.string.host_answer_label, item.hostAnswer),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
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
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        owner = item.owner,
        number = item.number,
        text = item.text,
        icon = vectorResource(Res.drawable.ic_trophy),
    )
}

@Composable
private fun QuestionCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    number: Int,
    owner: String,
    text: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = stringResource(Res.string.question_number_title, number, owner),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(topEnd = 20.dp, bottomStart = 20.dp),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                )
            }
        }
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(Res.string.question_text, text),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
        )
        content()
    }
}

@Composable
private fun QuestionCardButtons(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    id: String,
    first: String,
    second: String,
    onFirstClicked: (String) -> Unit,
    onSecondClicked: (String) -> Unit,
) {
    Row(modifier = modifier) {
        PrimaryCardButton(
            modifier = Modifier.weight(1f).padding(end = 32.dp),
            enabled = enabled,
            text = first,
            onClick = { onFirstClicked(id) }
        )
        SecondaryCardButton(
            modifier = Modifier.weight(1f).padding(32.dp),
            enabled = enabled,
            text = second,
            onClick = { onSecondClicked(id) }
        )
    }
}
