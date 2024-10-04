package com.palkesz.mr.x.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.theme.onPrimaryLight
import com.palkesz.mr.x.core.ui.theme.primaryLight
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.games.GameGraphRoute
import com.palkesz.mr.x.feature.home.HomeGraphRoute
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.game_controller
import mrx.composeapp.generated.resources.home
import mrx.composeapp.generated.resources.my_games
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun MrXBottomAppBar() {
	BottomAppBar(containerColor = MaterialTheme.colorScheme.primary) {
		Row(
			modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = 6.dp),
			horizontalArrangement = Arrangement.SpaceEvenly) {
			BottomAppBarItem(
				HomeGraphRoute.HomePage.route,
				stringResource(Res.string.home),
				Modifier.weight(1f),
				Icons.Filled.Home
			)

			Spacer(Modifier.weight(0.5f))

			BottomAppBarItem(
				GameGraphRoute.MyGamesPage.route,
				stringResource(Res.string.my_games),
				Modifier.weight(1f),
				vectorResource(Res.drawable.game_controller)
			)
		}
	}
}

@Composable
private fun BottomAppBarItem(
	destination: String,
	title: String,
	modifier: Modifier,
	icon: ImageVector) {
	val appState = LocalAppState.current
	val navController = LocalNavController.current
	val isSelected = appState.currentAppData.screenTitle == title
	val containerColor by animateColorAsState(
		targetValue = if (isSelected) {
			onPrimaryLight
		}
		else {
			primaryLight
		}
	)
	val contentColor by animateColorAsState(
		targetValue = if (isSelected) {
			primaryLight
		}
		else {
			onPrimaryLight
		}
	)
	Button(
		onClick = {
			navController?.navigate(destination)
		}, shape = RoundedCornerShape(20.dp),
		modifier = modifier,
		contentPadding = PaddingValues(all = 4.dp),
		colors = ButtonDefaults.buttonColors(
			containerColor = containerColor,
			contentColor = contentColor
		)
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				modifier = Modifier.size(32.dp)
			)
			Text(text = title)
		}
	}
}
