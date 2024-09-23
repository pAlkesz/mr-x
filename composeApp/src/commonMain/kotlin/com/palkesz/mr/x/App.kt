package com.palkesz.mr.x

import androidx.compose.runtime.Composable
import com.palkesz.mr.x.core.ui.theme.MrXTheme
import com.palkesz.mr.x.feature.app.MrXApp
import org.koin.compose.KoinContext


@Composable
fun App() {
	KoinContext {
		MrXTheme {
			MrXApp()
		}
	}
}
