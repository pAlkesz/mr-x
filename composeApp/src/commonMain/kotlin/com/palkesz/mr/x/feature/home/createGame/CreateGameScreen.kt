package com.palkesz.mr.x.feature.home.createGame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.DebouncedButton
import com.palkesz.mr.x.core.ui.components.FirstAndLastNameTexFields
import com.palkesz.mr.x.core.util.di.koinViewModel
import com.palkesz.mr.x.feature.app.LocalAppScope
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.app.LocalSnackBarHostState
import com.palkesz.mr.x.feature.games.GameScreenRoute
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.create_game
import mrx.composeapp.generated.resources.creation_in_progress
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateGameScreen(viewModel: CreateGameViewModel = koinViewModel<CreateGameViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsState()
    CreateGameScreenContent(
        viewState = viewState,
        onEventHandled = viewModel::onEventHandled,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onLastNameChanged = viewModel::onLastNameChanged,
        onCreateGameClicked = viewModel::onCreateGameClicked
    )
}

@Composable
fun CreateGameScreenContent(
    viewState: CreateGameViewState,
    onEventHandled: () -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onCreateGameClicked: () -> Unit
) {
    val appState = LocalAppState.current
    LaunchedEffect(Unit) {
        appState.apply {
            setScreenTitle(getString(Res.string.create_game))
            showTopAppBar()
            hideBottomAppBar()
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    HandleEvent(
        onEventHandled = onEventHandled,
        event = viewState.event
    )

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FirstAndLastNameTexFields(
            firstName = viewState.firstName,
            lastName = viewState.lastName,
            isFirstNameInvalid = viewState.isFirstNameInvalid,
            isLastNameInvalid = viewState.isLastNameInvalid,
            onFirstNameChanged = onFirstNameChanged,
            onLastNameChanged = onLastNameChanged,
            onDone = {
                keyboardController?.hide()
                onCreateGameClicked()
            }
        )
        DebouncedButton(
            onClick = {
                keyboardController?.hide()
                onCreateGameClicked()
            }, shape = RoundedCornerShape(
                topStartPercent = 100, topEndPercent = 5,
                bottomEndPercent = 100, bottomStartPercent = 5
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(Res.string.create_game),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun HandleEvent(
    onEventHandled: () -> Unit,
    event: CreateGameEvent?
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    when (event) {
        is CreateGameEvent.ValidationError -> LocalAppScope.current?.launch {
            snackbarHostState.showSnackbar(message = getString(event.message))
        }

        is CreateGameEvent.GameCreationInProgress -> {
            LocalAppScope.current?.launch {
                snackbarHostState.showSnackbar(message = getString(Res.string.creation_in_progress))
            }
            navController?.navigate(GameScreenRoute.MyGamesPage.route)
        }

        null -> return
    }
    onEventHandled()
}
