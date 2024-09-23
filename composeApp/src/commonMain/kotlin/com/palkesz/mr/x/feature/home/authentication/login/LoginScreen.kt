package com.palkesz.mr.x.feature.home.authentication.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.*
import com.palkesz.mr.x.di.koinViewModel
import com.palkesz.mr.x.feature.app.LocalAppScope
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.app.LocalSnackBarHostState
import com.palkesz.mr.x.feature.home.HomeScreenRoute
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel<LoginViewModelImpl>()) {
	val viewState by viewModel.viewState.collectAsState()
	LoginScreenContent(
		viewState = viewState,
		onEventHandled = viewModel::onEventHandled,
		onEmailChanged = viewModel::onEmailChanged,
		onPasswordChanged = viewModel::onPasswordChanged,
		onLoginClicked = viewModel::onLoginClicked,
		onSignupClicked = viewModel::onSignupClicked,
		onShowHidePasswordClicked = viewModel::onShowHidePasswordClicked
	)
}

@Composable
fun LoginScreenContent(
	viewState: LoginViewState,
	onEventHandled: () -> Unit,
	onEmailChanged: (String) -> Unit,
	onPasswordChanged: (String) -> Unit,
	onLoginClicked: () -> Unit,
	onSignupClicked: () -> Unit,
	onShowHidePasswordClicked: () -> Unit
) {
	val appState = LocalAppState.current
	LaunchedEffect(Unit) {
		appState.apply {
			hideTopAppBar()
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
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		Text(
			text = stringResource(Res.string.login_title),
			style = MaterialTheme.typography.headlineLarge,
			modifier = Modifier.padding(top = 24.dp),
			color = MaterialTheme.colorScheme.primary
		)
		BaseTextField(
			modifier = Modifier.fillMaxWidth(),
			value = viewState.email,
			isError = viewState.emailMessage != null,
			onValueChange = onEmailChanged,
			label = { Text(text = stringResource(Res.string.email_field_label)) },
			placeholder = { Text(text = stringResource(Res.string.email_field_placeholder)) },
			shape = CustomRoundedCornerShape(),
			keyboardCapitalization = KeyboardCapitalization.None,
			keyboardType = KeyboardType.Email,
			supportingText = {
				AnimatedNullability(viewState.emailMessage) {
					Text(text = stringResource(it))
				}
			}
		)
		PasswordTextField(
			modifier = Modifier.fillMaxWidth(),
			value = viewState.password,
			isError = viewState.passwordMessage != null,
			isPasswordShown = viewState.isPasswordShown,
			onValueChange = onPasswordChanged,
			shape = CustomRoundedCornerShapeMirrored(),
			supportingText = viewState.passwordMessage,
			onDone = {
				keyboardController?.hide()
				onLoginClicked()
			},
			onShowHidePasswordClicked = onShowHidePasswordClicked
		)
		ButtonWithLoading(
			isLoading = viewState.isLoading,
			onClick = {
				keyboardController?.hide()
				onLoginClicked()
			}
		)
		TextButton(onClick = onSignupClicked) {
			Text(text = stringResource(Res.string.navigate_to_signup))
		}
	}
}

@Composable
fun HandleEvent(
	onEventHandled: () -> Unit,
	event: LoginEvent?
) {
	event?.let { event ->
		val snackbarHostState = LocalSnackBarHostState.current
		val navController = LocalNavController.current
		when (event) {
			is LoginEvent.LoginSuccess -> {
				LocalAppScope.current?.launch {
					snackbarHostState.showSnackbar(message = getString(Res.string.login_success_message))
				}
				navController?.navigate(HomeScreenRoute.HomePage.route)
			}
			is LoginEvent.NavigateToSignup -> navController?.navigate(HomeScreenRoute.Signup.route)
		}
		onEventHandled()
	}
}
