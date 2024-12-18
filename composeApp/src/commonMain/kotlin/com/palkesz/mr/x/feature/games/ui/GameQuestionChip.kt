package com.palkesz.mr.x.feature.games.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_question_head
import mrx.composeapp.generated.resources.ic_question_mark
import org.jetbrains.compose.resources.vectorResource

@Composable
fun GameQuestionChip(
    modifier: Modifier = Modifier,
    questionCount: Int,
    barkochbaCount: Int,
    containerColor: Color,
) {
    val contentColor = MaterialTheme.colorScheme.contentColorFor(backgroundColor = containerColor)
    Row(
        modifier = modifier
            .height(height = 32.dp)
            .border(
                width = 1.dp,
                color = contentColor,
                shape = RoundedCornerShape(size = 8.dp),
            )
            .clip(shape = RoundedCornerShape(size = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = contentColor, shape = GameQuestionChipShape),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                imageVector = vectorResource(resource = Res.drawable.ic_question_mark),
                contentDescription = null,
                tint = containerColor,
            )
            Text(
                text = questionCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = containerColor,
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp),
            text = barkochbaCount.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
        )
        Icon(
            modifier = Modifier.padding(end = 8.dp),
            imageVector = vectorResource(resource = Res.drawable.ic_question_head),
            contentDescription = null,
            tint = contentColor,
        )
    }
}

private object GameQuestionChipShape : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Generic(path = with(density) {
        Path().apply {
            lineTo(x = size.width + 16.dp.toPx(), y = 0f)
            lineTo(x = size.width, y = size.height)
            lineTo(x = 0f, y = size.height)
            close()
        }
    })
}
