package com.mr.x

import androidx.compose.runtime.Composable
import com.mr.x.core.ui.theme.MrXTheme
import com.mr.x.feature.app.MrXApp
import org.koin.compose.KoinContext


@Composable
fun App() {
	KoinContext {
		MrXTheme {
			MrXApp()
		}
	}
}
