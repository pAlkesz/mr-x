package com.palkesz.mr.x.core.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenWidth() = LocalConfiguration.current.screenWidthDp.dp
