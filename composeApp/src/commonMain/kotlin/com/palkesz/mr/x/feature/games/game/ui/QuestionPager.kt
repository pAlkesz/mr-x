package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.helpers.bold
import com.palkesz.mr.x.core.ui.helpers.getScreenWidth
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.barkochba_question_tab_title
import mrx.composeapp.generated.resources.normal_question_tab_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuestionPager(
    modifier: Modifier = Modifier,
    normalPage: @Composable () -> Unit,
    barkochbaPage: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        val pagerState = rememberPagerState { 2 }
        val coroutineScope = rememberCoroutineScope()
        QuestionTabs(
            modifier = Modifier.padding(horizontal = 16.dp),
            selectedItemIndex = pagerState.currentPage,
            onClick = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = index,
                        animationSpec = tween(easing = LinearEasing),
                    )
                }
            },
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) { index ->
            if (index == 0) normalPage() else barkochbaPage()
        }
    }
}

@Composable
private fun QuestionTabs(
    modifier: Modifier = Modifier,
    selectedItemIndex: Int,
    onClick: (Int) -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceBright,
                shape = RoundedCornerShape(size = 16.dp),
            )
            .height(intrinsicSize = IntrinsicSize.Min),
    ) {
        val tabWidth = (getScreenWidth() / 2f) - 20.dp
        val indicatorOffset: Dp by animateDpAsState(
            targetValue = tabWidth * selectedItemIndex,
            animationSpec = tween(easing = LinearEasing),
        )
        TabIndicator(width = tabWidth, offset = indicatorOffset)
        Row(modifier = Modifier.fillMaxWidth()) {
            TabItem(
                modifier = Modifier.weight(1f),
                isSelected = selectedItemIndex == 0,
                onClick = { onClick(0) },
                text = stringResource(Res.string.normal_question_tab_title),
            )
            TabItem(
                modifier = Modifier.weight(1f),
                isSelected = selectedItemIndex == 1,
                onClick = { onClick(1) },
                text = stringResource(Res.string.barkochba_question_tab_title),
            )
        }
    }
}

@Composable
private fun TabItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    val textColor: Color by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(easing = LinearEasing),
    )
    Text(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        text = text,
        style = LocalTextStyle.current.bold(),
        color = textColor,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun TabIndicator(width: Dp, offset: Dp) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(all = 4.dp)
            .width(width = width)
            .offset(x = offset)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(size = 14.dp),
            )
    )
}
