package com.palkesz.mr.x.feature.home.authentication.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.palkesz.mr.x.core.ui.components.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.BaseTextField
import com.palkesz.mr.x.core.ui.components.ButtonWithLoading
import com.palkesz.mr.x.core.ui.components.CustomRoundedCornerShape
import com.palkesz.mr.x.core.ui.components.CustomRoundedCornerShapeMirrored
import com.palkesz.mr.x.core.ui.components.PasswordTextField
import com.palkesz.mr.x.core.util.di.koinViewModel
import com.palkesz.mr.x.feature.app.LocalAppScope
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController
import com.palkesz.mr.x.feature.app.LocalSnackBarHostState
import com.palkesz.mr.x.feature.home.HomeScreenRoute
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.email_field_label
import mrx.composeapp.generated.resources.email_field_placeholder
import mrx.composeapp.generated.resources.navigate_to_login
import mrx.composeapp.generated.resources.sign_up_title
import mrx.composeapp.generated.resources.signup_success_message
import mrx.composeapp.generated.resources.username_field_label
import mrx.composeapp.generated.resources.username_field_placeholder
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignupScreen(viewModel: SignupViewModel = koinViewModel<SignupViewModelImpl>()) {
	val viewState by viewModel.viewState.collectAsState()
	SignupScreenContent(
		viewState = viewState,
		onEventHandled = viewModel::onEventHandled,
		onEmailChanged = viewModel::onEmailChanged,
		onUsernameChanged = viewModel::onUsernameChanged,
		onPasswordChanged = viewModel::onPasswordChanged,
		onLoginClicked = viewModel::onLoginClicked,
		onSignupClicked = viewModel::onSignupClicked,
		onShowHidePasswordClicked = viewModel::onShowHidePasswordClicked
	)
}

@Composable
fun SignupScreenContent(
	viewState: SignupViewState,
	onEventHandled: () -> Unit,
	onEmailChanged: (String) -> Unit,
	onUsernameChanged: (String) -> Unit,
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
			text = stringResource(Res.string.sign_up_title),
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
		BaseTextField(
			modifier = Modifier.fillMaxWidth(),
			value = viewState.username,
			isError = viewState.usernameMessage != null,
			onValueChange = onUsernameChanged,
			label = { Text(text = stringResource(Res.string.username_field_label)) },
			placeholder = { Text(text = stringResource(Res.string.username_field_placeholder)) },
			shape = CustomRoundedCornerShapeMirrored(),
			keyboardCapitalization = KeyboardCapitalization.None,
			supportingText = {
				AnimatedNullability(viewState.usernameMessage) {
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
			supportingText = viewState.passwordMessage,
			onDone = {
				keyboardController?.hide()
				onSignupClicked()
			},
			onShowHidePasswordClicked = onShowHidePasswordClicked,
		)
		ButtonWithLoading(
			isLoading = viewState.isLoading,
			shape = CustomRoundedCornerShapeMirrored(),
			onClick = {
				keyboardController?.hide()
				onSignupClicked()
			}
		)
		TextButton(onClick = onLoginClicked) {
			Text(text = stringResource(Res.string.navigate_to_login))
		}
	}
}

@Composable
fun HandleEvent(
	onEventHandled: () -> Unit,
	event: SignupEvent?
) {
	event?.let { event ->
		val snackbarHostState = LocalSnackBarHostState.current
		val navController = LocalNavController.current
		when (event) {
			is SignupEvent.SignupSuccess -> {
				LocalAppScope.current?.launch {
					snackbarHostState.showSnackbar(message = getString(Res.string.signup_success_message))
				}
				navController?.navigate(HomeScreenRoute.HomePage.route)
			}
			is SignupEvent.NavigateToLogin -> navController?.navigate(HomeScreenRoute.Login.route)
		}
		onEventHandled()
	}
}
