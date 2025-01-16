package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.badge.NotificationBadge
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
    pagerState: PagerState,
    questionBadgeCount: Int?,
    barkochbaBadgeCount: Int?,
    onPageSelected: (Int) -> Unit,
    normalPage: @Composable () -> Unit,
    barkochbaPage: @Composable () -> Unit,
) {
    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            onPageSelected(page)
        }
    }
    Column(modifier = modifier) {
        val coroutineScope = rememberCoroutineScope()
        QuestionTabs(
            modifier = Modifier.padding(horizontal = 16.dp),
            selectedItemIndex = pagerState.currentPage,
            questionBadgeCount = questionBadgeCount,
            barkochbaBadgeCount = barkochbaBadgeCount,
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
    questionBadgeCount: Int?,
    barkochbaBadgeCount: Int?,
    onClick: (Int) -> Unit,
) {
    QuestionTabLayout(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().zIndex(zIndex = 1f)) {
            TabItem(
                modifier = Modifier.weight(1f),
                isSelected = selectedItemIndex == 0,
                onClick = { onClick(0) },
                text = stringResource(Res.string.normal_question_tab_title),
                badgeCount = questionBadgeCount,
            )
            TabItem(
                modifier = Modifier.weight(1f),
                isSelected = selectedItemIndex == 1,
                onClick = { onClick(1) },
                text = stringResource(Res.string.barkochba_question_tab_title),
                badgeCount = barkochbaBadgeCount,
            )
        }
        val tabWidth = (getScreenWidth() / 2f) - 20.dp
        val indicatorOffset: Dp by animateDpAsState(
            targetValue = tabWidth * selectedItemIndex,
            animationSpec = tween(easing = LinearEasing),
        )
        TabIndicator(width = tabWidth, offset = indicatorOffset)
    }
}

@Composable
private fun TabItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    text: String,
    badgeCount: Int?,
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
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(vertical = 12.dp),
            text = text,
            style = LocalTextStyle.current.bold(),
            color = textColor,
            textAlign = TextAlign.Center
        )
        AnimatedNullability(item = badgeCount) { count ->
            NotificationBadge(modifier = Modifier.padding(start = 8.dp), count = count)
        }
    }
}

@Composable
private fun TabIndicator(width: Dp, offset: Dp) {
    Box(
        modifier = Modifier
            .padding(all = 4.dp)
            .width(width = width)
            .offset(x = offset)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(size = 14.dp),
            )
    )
}
