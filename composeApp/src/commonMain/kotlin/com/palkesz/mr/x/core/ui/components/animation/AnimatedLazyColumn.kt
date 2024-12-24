package com.palkesz.mr.x.core.ui.components.animation

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.helpers.getScreenWidth
import com.palkesz.mr.x.core.ui.modifiers.conditional
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.absoluteValue

@Composable
fun <T> AnimatedLazyColumn(
    modifier: Modifier = Modifier,
    items: ImmutableList<T>,
    animatedItemKey: String?,
    getKey: T.() -> String,
    listItem: @Composable (Int, T) -> Unit,
) {
    var isAnimationDone by rememberSaveable(key = animatedItemKey) { mutableStateOf(value = false) }
    val state = rememberLazyListState()
    val screenWidth = getScreenWidth()
    val animatedItemOffset by animateIntAsState(
        animationSpec = tween(durationMillis = ANIMATION_LENGTH),
        targetValue = if (isAnimationDone) 0 else (screenWidth.value / 2f).toInt(),
    )
    LaunchedEffect(key1 = Unit) {
        isAnimationDone = true
        state.scrollToItem(index = 0)
    }
    LazyColumn(modifier = modifier, state = state) {
        itemsIndexed(
            items = items.filter { it.getKey() != animatedItemKey || isAnimationDone },
            key = { _, item -> item.getKey() }) { index, item ->
            Box(
                modifier = Modifier
                    .animateItem(fadeInSpec = tween(durationMillis = ANIMATION_LENGTH))
                    .conditional(condition = item.getKey() == animatedItemKey) {
                        offset(x = -animatedItemOffset.dp)
                    }
                    .alpha(alpha = state.layoutInfo.getItemAlpha(key = item.getKey())),
            ) {
                listItem(index, item)
            }
        }
    }
}

private fun LazyListLayoutInfo.getItemAlpha(key: String) =
    1 - (visibleItemsInfo.find { it.key == key }?.let { item ->
        val offset = item.offset.toFloat()
        val size = item.size.toFloat()
        when {
            offset < 0 -> {
                offset.absoluteValue / size
            }

            item.offset + item.size > viewportEndOffset -> {
                1 - ((offset - viewportEndOffset).absoluteValue / size)
            }

            else -> 0f
        }
    } ?: 0f)

private const val ANIMATION_LENGTH = 1500
