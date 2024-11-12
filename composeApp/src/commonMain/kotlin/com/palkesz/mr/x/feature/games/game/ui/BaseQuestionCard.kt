package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.palkesz.mr.x.core.ui.components.button.PrimaryCardButton
import com.palkesz.mr.x.core.ui.components.button.SecondaryCardButton
import com.palkesz.mr.x.core.util.extensions.capitalizeFirstChar
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.question_number_title
import mrx.composeapp.generated.resources.question_text
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuestionCard(
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                text = stringResource(Res.string.question_number_title, number, owner),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Box(
                modifier = Modifier
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
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = stringResource(Res.string.question_text, text.capitalizeFirstChar()),
            style = MaterialTheme.typography.bodyMedium,
        )
        content()
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun QuestionCardButtons(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    id: String,
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
            modifier = Modifier.weight(1f),
            enabled = enabled,
            text = first,
            onClick = { onFirstClicked(id) },
        )
        Spacer(modifier = Modifier.size(32.dp))
        SecondaryCardButton(
            modifier = Modifier.weight(1f),
            enabled = enabled,
            text = second,
            onClick = { onSecondClicked(id) },
        )
    }
}
