package com.palkesz.mr.x.feature.home.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.util.di.koinViewModel
import com.palkesz.mr.x.feature.app.LocalAppScope
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.app.LocalSnackBarHostState
import com.palkesz.mr.x.feature.home.HomeScreenRoute
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.login_success_message
import mrx.composeapp.generated.resources.spy
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.vectorResource

@Composable
fun LoadingScreen(viewModel: LoadingViewModel = koinViewModel<LoadingViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsState()
    LoadingScreenContent(
        viewState = viewState,
        onEventHandled = viewModel::onEventHandled
    )
}

@Composable
fun LoadingScreenContent(
    viewState: LoadingViewState,
    onEventHandled: () -> Unit
) {

    HandleEvent(
        onEventHandled = onEventHandled,
        event = viewState.event
    )

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)) {
        Icon(
            imageVector = vectorResource(Res.drawable.spy),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).size(300.dp),
        )
    }
}

@Composable
fun HandleEvent(
    onEventHandled: () -> Unit,
    event: LoadingEvent?
) {
    event?.let { event ->
        val snackbarHostState = LocalSnackBarHostState.current
        val navController = LocalNavController.current
        when (event) {
            is LoadingEvent.LoginSuccess -> {
                LocalAppScope.current?.launch {
                    snackbarHostState.showSnackbar(message = getString(Res.string.login_success_message))
                }
                navController?.navigate(HomeScreenRoute.HomePage.route)
            }

            is LoadingEvent.LoginFailure -> navController?.navigate(HomeScreenRoute.Login.route)
        }
        onEventHandled()
    }
}
