package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun QuestionTabLayout(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.surfaceBright,
            shape = RoundedCornerShape(size = 16.dp),
        ),
        content = content,
        measurePolicy = QuestionTabMeasurePolicy,
    )
}

private object QuestionTabMeasurePolicy : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val tabRow = measurables.first().measure(constraints = constraints)
        val tabIndicator =
            measurables[1].measure(constraints = constraints.copy(minHeight = tabRow.height))
        return layout(width = tabRow.width, height = tabRow.height) {
            tabRow.placeRelative(x = 0, y = 0)
            tabIndicator.placeRelative(x = 0, y = 0)
        }
    }
}
