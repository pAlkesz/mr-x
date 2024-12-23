package com.palkesz.mr.x.core.ui.components.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlin.math.max

@Composable
fun BackwardsRowLayout(
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = false,
    minChildWidth: Dp,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = BackwardsRowMeasurePolicy(
            minChildWidth = minChildWidth,
            fillMaxWidth = fillMaxWidth
        )
    )
}

private class BackwardsRowMeasurePolicy(
    private val minChildWidth: Dp,
    private val fillMaxWidth: Boolean,
) : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val placeables = measurables.map { measurable ->
            measurable.measure(
                constraints = constraints.copy(
                    minWidth = 0,
                    maxWidth = constraints.maxWidth - minChildWidth.toPx().toInt()
                )
            )
        }
        val maxRowWidth = constraints.getMaxWidth()
        val rowWidth = if (fillMaxWidth) {
            max(a = maxRowWidth, b = placeables.sumOf { it.width })
        } else {
            placeables.sumOf { it.width }
        }
        val rowHeight = placeables.maxOf { it.height }
        return layout(width = rowWidth, height = placeables.maxOf { it.height }) {
            var positionX = rowWidth
            placeables.reversed().forEach { placeable ->
                placeable.placeRelative(
                    x = positionX - placeable.width,
                    y = rowHeight - placeable.height
                )
                positionX -= placeable.width
            }
        }
    }

    private fun Constraints.getMaxWidth() =
        (if (maxWidth == Constraints.Infinity) minWidth else maxWidth).coerceAtLeast(minimumValue = 0)
}
