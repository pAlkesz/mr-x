package com.palkesz.mr.x.feature.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.palkesz.mr.x.core.ui.components.MrXBottomAppBar
import com.palkesz.mr.x.core.ui.components.MrXTopAppBar
import com.palkesz.mr.x.di.koinViewModel
import com.palkesz.mr.x.feature.games.GameScreenRoute
import com.palkesz.mr.x.feature.games.myGamesScreenNavigation
import com.palkesz.mr.x.feature.home.homeScreenNavigation
import dev.theolm.rinku.compose.ext.DeepLinkListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MrXApp(
	appViewModel: AppViewModel = koinViewModel<AppViewModel>()
) {
	val viewState by appViewModel.viewState.collectAsState()
	val appState = LocalAppState.current
	val snackbarHostState = LocalSnackBarHostState.current
	val appScope = rememberCoroutineScope()
	val navController: NavHostController = rememberNavController()
	DeepLinkListener { deepLink ->
		appViewModel.onDeepLinkReceived(deepLink)
	}

	CompositionLocalProvider(
		LocalAppState provides appState,
		LocalSnackBarHostState provides snackbarHostState,
		LocalNavController provides navController,
		LocalAppScope provides appScope
	) {
		val bottomBarOffset by animateDpAsState(
			targetValue = if (appState.currentAppData.isBottomAppBarVisible)
				80.dp
			else
				0.dp)
		val topBarOffset by animateDpAsState(
			targetValue = if (appState.currentAppData.isTopAppBarVisible)
				64.dp
			else
				0.dp)
		HandleEvent(
			onEventHandled = appViewModel::onEventHandled,
			event = viewState.event
		)
		Scaffold(
			snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
			topBar = {
				AnimatedVisibility(
					visible = appState.currentAppData.isTopAppBarVisible,
					enter = slideInVertically(initialOffsetY = { -it }),
					exit = slideOutVertically(targetOffsetY = { -it })) {
					MrXTopAppBar()
				}
			},
			bottomBar = {
				AnimatedVisibility(
					appState.currentAppData.isBottomAppBarVisible,
					enter = slideInVertically(initialOffsetY = { it }),
					exit = slideOutVertically(targetOffsetY = { it })) {
					MrXBottomAppBar()
				}
			}
		) { innerPadding ->
			NavHost(
				navController = navController,
				startDestination = MrXGraph.HOME,
				route = MrXGraph.ROOT,
				modifier = Modifier.padding(
					start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
					top = topBarOffset,
					end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
					bottom = bottomBarOffset)
			) {
				homeScreenNavigation()
				myGamesScreenNavigation()
			}
		}
	}
}


val LocalAppState = compositionLocalOf {
	AppState()
}
val LocalSnackBarHostState = compositionLocalOf {
	SnackbarHostState()
}
val LocalNavController = compositionLocalOf<NavHostController?> {
	null
}
val LocalAppScope = compositionLocalOf<CoroutineScope?> {
	null
}

@Composable
fun HandleEvent(
	event: AppEvent?,
	onEventHandled: () -> Unit
) {
	val snackbarHostState = LocalSnackBarHostState.current
	val localAppScope = LocalAppScope.current
	val navController = LocalNavController.current

	when (event) {
		is AppEvent.ErrorOccurred -> {
			localAppScope?.launch {
				snackbarHostState.currentSnackbarData?.dismiss()
				snackbarHostState.showSnackbar(event.message)
			}
		}
		is AppEvent.DeepLinkReceived -> {
			navController?.navigate(GameScreenRoute.MyGamesPage.route)
		}
		null -> {}
	}
	onEventHandled()
}

object MrXGraph {
	const val ROOT = "root_graph"
	const val HOME = "home_graph"
	const val GAMES = "games_graph"
}

