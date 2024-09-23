package com.palkesz.mr.x.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.palkesz.mr.x.feature.app.MrXGraph
import com.palkesz.mr.x.feature.home.authentication.login.LoginScreen
import com.palkesz.mr.x.feature.home.authentication.signup.SignupScreen
import com.palkesz.mr.x.feature.home.createGame.CreateGameScreen
import com.palkesz.mr.x.feature.home.loading.LoadingScreen
import com.palkesz.mr.x.feature.home.rules.RulesScreen
import com.palkesz.mr.x.feature.home.scanQrCode.ScanQrCodeScreen

fun NavGraphBuilder.homeScreenNavigation() {
	navigation(
		startDestination = HomeScreenRoute.Loading.route,
		route = MrXGraph.HOME
	) {
		composable(route = HomeScreenRoute.Loading.route) {
			LoadingScreen()
		}
		composable(route = HomeScreenRoute.Login.route) {
			LoginScreen()
		}
		composable(route = HomeScreenRoute.Signup.route) {
			SignupScreen()
		}
		composable(route = HomeScreenRoute.HomePage.route) {
			HomeScreen()
		}
		composable(route = HomeScreenRoute.CreateGame.route) {
			CreateGameScreen()
		}
		composable(route = HomeScreenRoute.JoinGame.route) {
			ScanQrCodeScreen()
		}
		composable(route = HomeScreenRoute.Rules.route) {
			RulesScreen()
		}
	}
}

sealed class HomeScreenRoute(val route: String) {
	data object Loading : HomeScreenRoute("LOADING")
	data object Login : HomeScreenRoute("LOGIN")
	data object Signup : HomeScreenRoute("SIGNUP")
	data object HomePage : HomeScreenRoute("HOME_PAGE")
	data object CreateGame : HomeScreenRoute("CREATE_GAME")
	data object JoinGame : HomeScreenRoute("JOIN_GAME")
	data object Rules : HomeScreenRoute("RULES")
}
