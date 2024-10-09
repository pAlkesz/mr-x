package com.palkesz.mr.x.feature.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.helpers.showSnackbar
import com.palkesz.mr.x.core.ui.providers.LocalAppScope
import com.palkesz.mr.x.core.ui.providers.LocalAppState
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.ui.providers.LocalSnackBarHostState
import com.palkesz.mr.x.feature.app.appbars.MrXBottomAppBar
import com.palkesz.mr.x.feature.app.appbars.MrXTopAppBar
import com.palkesz.mr.x.feature.app.appbars.OfflineAppBar
import com.palkesz.mr.x.feature.authentication.AuthGraphRoute
import com.palkesz.mr.x.feature.authentication.authGraphNavigation
import com.palkesz.mr.x.feature.games.GameGraphRoute
import com.palkesz.mr.x.feature.games.myGamesGraphNavigation
import com.palkesz.mr.x.feature.home.HomeGraphRoute
import com.palkesz.mr.x.feature.home.homeGraphNavigation
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.login_success_message
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

object MrXGraph {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val HOME = "home_graph"
    const val GAMES = "games_graph"
}

@Composable
fun MrXApp(viewModel: AppViewModel = koinViewModel<AppViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsState()
    MrXAppContent(
        state = viewState,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun MrXAppContent(
    state: AppViewState,
    onEventHandled: () -> Unit,
) {
    val appState = LocalAppState.current
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalAppState provides appState,
        LocalSnackBarHostState provides snackbarHostState,
        LocalNavController provides navController,
        LocalAppScope provides rememberCoroutineScope(),
    ) {
        HandleEvent(onEventHandled = onEventHandled, event = state.event)
        Scaffold(
            modifier = Modifier.imePadding(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = { MrXTopAppBar() },
            bottomBar = { MrXBottomAppBar() },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                AnimatedVisibility(visible = state.isOfflineBarVisible) {
                    OfflineAppBar()
                }
                NavHost(
                    navController = navController,
                    startDestination = if (state.isLoggedIn) MrXGraph.HOME else MrXGraph.AUTH,
                    route = MrXGraph.ROOT,
                ) {
                    authGraphNavigation()
                    homeGraphNavigation()
                    myGamesGraphNavigation()
                }
            }
        }
    }
}

@Composable
private fun HandleEvent(event: AppEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(key1 = event) { appScope, snackbarHostState, navController ->
        when (event) {
            null -> return@HandleEventEffect
            is AppEvent.ShowSnackbar -> {
                appScope?.showSnackbar(
                    snackbarHostState = snackbarHostState,
                    message = event.message,
                )
            }

            is AppEvent.NavigateToHome -> {
                appScope?.showSnackbar(
                    snackbarHostState = snackbarHostState,
                    message = getString(Res.string.login_success_message),
                )
                navController?.navigate(HomeGraphRoute.HomePage.route)
            }

            is AppEvent.NavigateToMyGames -> {
                navController?.navigate(GameGraphRoute.MyGamesPage.route)
            }

            is AppEvent.NavigateToAddUsername -> {
                navController?.navigate(AuthGraphRoute.AddUsername.route)
            }
        }
        onEventHandled()
    }
}
