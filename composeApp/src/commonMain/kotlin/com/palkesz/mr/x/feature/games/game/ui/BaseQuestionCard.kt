package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.button.PrimaryCardButton
import com.palkesz.mr.x.core.ui.components.button.SecondaryCardButton
import com.palkesz.mr.x.core.ui.components.layout.BackwardsRowLayout
import com.palkesz.mr.x.core.ui.helpers.bold
import com.palkesz.mr.x.core.util.extensions.capitalizeFirstChar
import com.palkesz.mr.x.core.util.extensions.capitalizeWords
import com.palkesz.mr.x.feature.games.ui.GameIcon
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.expected_answer_label
import mrx.composeapp.generated.resources.ic_correct_answer
import mrx.composeapp.generated.resources.ic_host
import mrx.composeapp.generated.resources.ic_player
import mrx.composeapp.generated.resources.question_number_title
import mrx.composeapp.generated.resources.question_text
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun QuestionCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    number: Int,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(size = 20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                text = stringResource(Res.string.question_number_title, number),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            QuestionStatusIcon(icon = icon)
        }
        content()
        Spacer(modifier = Modifier.size(size = 16.dp))
    }
}

@Composable
fun QuestionCardButtons(
    modifier: Modifier = Modifier,
    id: String,
    enabled: Boolean = true,
    first: String,
    second: String,
    onFirstClicked: (String) -> Unit,
    onSecondClicked: (String) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        PrimaryCardButton(
            modifier = Modifier.weight(weight = 1f),
            enabled = enabled,
            text = first,
            onClick = { onFirstClicked(id) },
        )
        Spacer(modifier = Modifier.size(size = 32.dp))
        SecondaryCardButton(
            modifier = Modifier.weight(weight = 1f),
            enabled = enabled,
            text = second,
            onClick = { onSecondClicked(id) },
        )
    }
}

@Composable
fun QuestionText(modifier: Modifier = Modifier, text: String, owner: String) {
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        GameIcon(
            isBig = false,
            icon = vectorResource(Res.drawable.ic_player),
        )
        QuestionTextBubble(
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
            text = stringResource(Res.string.question_text, text.capitalizeFirstChar()),
            owner = owner,
            icon = null,
        )
    }
}

@Composable
fun ExpectedAnswerText(modifier: Modifier = Modifier, text: String, owner: String) {
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        GameIcon(
            isBig = false,
            icon = vectorResource(Res.drawable.ic_player),
        )
        QuestionTextBubble(
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
            text = stringResource(Res.string.expected_answer_label, text.capitalizeWords()),
            owner = owner,
            icon = vectorResource(Res.drawable.ic_correct_answer),
        )
    }
}

@Composable
fun AnswerText(
    modifier: Modifier = Modifier,
    text: String,
    owner: String,
    isHost: Boolean,
    icon: ImageVector? = null,
) {
    BackwardsRowLayout(
        modifier = modifier,
        fillMaxWidth = true,
        minChildWidth = 32.dp
    ) {
        QuestionTextBubble(
            modifier = Modifier.padding(start = 40.dp, end = 8.dp, bottom = 4.dp),
            text = text.capitalizeWords(),
            owner = owner,
            icon = icon,
        )
        GameIcon(
            isBig = false,
            icon = vectorResource(if (isHost) Res.drawable.ic_host else Res.drawable.ic_player),
        )
    }
}

@Composable
private fun QuestionTextBubble(
    modifier: Modifier = Modifier,
    text: String,
    owner: String,
    icon: ImageVector?,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(size = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        BackwardsRowLayout(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            minChildWidth = 28.dp,
        ) {
            Column {
                Text(
                    text = owner,
                    style = MaterialTheme.typography.bodySmall.bold(),
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            AnimatedNullability(item = icon) {
                Image(
                    modifier = Modifier.padding(start = 4.dp),
                    imageVector = it,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun QuestionStatusIcon(modifier: Modifier = Modifier, icon: ImageVector) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(topEnd = 20.dp, bottomStart = 20.dp),
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiary,
        )
    }
}
