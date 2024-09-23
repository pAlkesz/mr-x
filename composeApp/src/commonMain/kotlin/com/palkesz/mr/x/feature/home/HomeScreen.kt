package com.palkesz.mr.x.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.palkesz.mr.x.core.ui.components.BaseCard
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController
import mrx.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString

@Composable
fun HomeScreen() {
	val appState = LocalAppState.current
	val navController = LocalNavController.current
	LaunchedEffect(Unit) {
		appState.apply {
			hideTopAppBar()
			showBottomAppBar()
			setScreenTitle(getString(Res.string.home))
		}
	}
	Column(
		modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		BaseCard(
			modifier = Modifier.fillMaxWidth(),
			onClick = { navController?.navigate(HomeScreenRoute.CreateGame.route) },
			title = Res.string.create_game,
			description = Res.string.create_game_descr,
			buttonLabel = Res.string.create_game
		)
		BaseCard(
			onClick = { navController?.navigate(HomeScreenRoute.JoinGame.route) },
			title = Res.string.join_game,
			description = Res.string.join_game_descr,
			buttonLabel = Res.string.join_game
		)
		BaseCard(
			onClick = { navController?.navigate(HomeScreenRoute.Rules.route) },
			title = Res.string.rules_title,
			description = Res.string.rules_card_description,
			buttonLabel = Res.string.rules_button_label
		)
	}
}
