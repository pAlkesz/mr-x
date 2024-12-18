package com.palkesz.mr.x.core.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import platform.UIKit.UIScreen

@Composable
actual fun getScreenWidth() = LocalWindowInfo.current.containerSize.width.pxToPoint().dp

private fun Int.pxToPoint() = (this.toDouble() / UIScreen.mainScreen.scale).toInt()
