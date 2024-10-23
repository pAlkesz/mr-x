package com.palkesz.mr.x.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.palkesz.mr.x.feature.app.MrXGraph
import com.palkesz.mr.x.feature.home.create.CreateGameScreen
import com.palkesz.mr.x.feature.home.join.JoinGameScreen
import com.palkesz.mr.x.feature.home.rules.RulesScreen

fun NavGraphBuilder.homeGraphNavigation() {
    navigation(startDestination = HomeGraphRoute.HomePage.route, route = MrXGraph.HOME) {
        composable(route = HomeGraphRoute.HomePage.route) {
            HomeScreen()
        }
        composable(route = HomeGraphRoute.CreateGame.route) {
            CreateGameScreen()
        }
        composable(route = HomeGraphRoute.JoinGame.route) {
            JoinGameScreen()
        }
        composable(route = HomeGraphRoute.Rules.route) {
            RulesScreen()
        }
    }
}

sealed class HomeGraphRoute(val route: String) {

    data object HomePage : HomeGraphRoute("HOME_PAGE")

    data object CreateGame : HomeGraphRoute("CREATE_GAME")

    data object JoinGame : HomeGraphRoute("JOIN_GAME")

    data object Rules : HomeGraphRoute("RULES")
}
