package com.palkesz.mr.x.feature.authentication

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.palkesz.mr.x.feature.app.MrXGraph
import com.palkesz.mr.x.feature.authentication.login.LoginScreen
import com.palkesz.mr.x.feature.authentication.username.AddUsernameScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.authGraphNavigation() {
    navigation(startDestination = AuthGraph.Login, route = MrXGraph.Auth::class) {
        composable<AuthGraph.Login> {
            LoginScreen()
        }
        composable<AuthGraph.AddUsername> {
            AddUsernameScreen()
        }
    }
}

sealed interface AuthGraph {

    @Serializable
    data object Login : AuthGraph

    @Serializable
    data object AddUsername : AuthGraph

}
