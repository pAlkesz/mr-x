package com.palkesz.mr.x.feature.authentication

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.palkesz.mr.x.feature.app.MrXGraph
import com.palkesz.mr.x.feature.authentication.login.LoginScreen
import com.palkesz.mr.x.feature.authentication.username.AddUsernameScreen

fun NavGraphBuilder.authGraphNavigation() {
    navigation(startDestination = AuthGraphRoute.Login.route, route = MrXGraph.AUTH) {
        composable(route = AuthGraphRoute.Login.route) {
            LoginScreen()
        }
        composable(route = AuthGraphRoute.AddUsername.route) {
            AddUsernameScreen()
        }
    }
}

sealed class AuthGraphRoute(
    val route: String,
) {
    data object Login : AuthGraphRoute("LOGIN")

    data object AddUsername : AuthGraphRoute("ADDUSERNAME")

}
