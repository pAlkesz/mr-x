package com.palkesz.mr.x.feature.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.providers.LocalAppScope
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.ui.providers.LocalSnackBarHostState
import com.palkesz.mr.x.feature.app.appbars.MrXBottomAppBar
import com.palkesz.mr.x.feature.app.appbars.MrXTopAppBar
import com.palkesz.mr.x.feature.app.appbars.OfflineAppBar
import com.palkesz.mr.x.feature.authentication.authGraphNavigation
import com.palkesz.mr.x.feature.games.GameGraph
import com.palkesz.mr.x.feature.games.gamesGraphNavigation
import com.palkesz.mr.x.feature.home.homeGraphNavigation
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MrXApp(viewModel: AppViewModel = koinViewModel<AppViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    MrXAppContent(
        state = viewState,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun MrXAppContent(state: AppViewState, onEventHandled: () -> Unit) {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalAppScope provides rememberCoroutineScope()
    ) {
        HandleEvent(onEventHandled = onEventHandled, event = state.event)
        Scaffold(
            modifier = Modifier.imePadding(),
            snackbarHost = { SnackbarHost(hostState = LocalSnackBarHostState.current) },
            topBar = { MrXTopAppBar() },
            bottomBar = { MrXBottomAppBar() },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                AnimatedVisibility(visible = state.isOfflineBarVisible) {
                    OfflineAppBar()
                }
                NavHost(
                    navController = navController,
                    startDestination = if (state.isLoggedIn) MrXGraph.Home else MrXGraph.Auth,
                ) {
                    authGraphNavigation()
                    homeGraphNavigation()
                    gamesGraphNavigation()
                }
            }
        }
    }
}

@Composable
private fun HandleEvent(event: AppEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(key1 = event) { appEvent, _, _, navController ->
        when (appEvent) {
            is AppEvent.NavigateToGames -> {
                navController?.navigate(route = GameGraph.Games(joinedGameId = appEvent.gameId))
            }
        }
        onEventHandled()
    }
}
