package com.palkesz.mr.x.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.palkesz.mr.x.feature.app.MrXGraph
import com.palkesz.mr.x.feature.home.create.CreateGameScreen
import com.palkesz.mr.x.feature.home.join.JoinGameScreen
import com.palkesz.mr.x.feature.home.tutorial.TutorialScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.homeGraphNavigation() {
    navigation(startDestination = HomeGraph.Home, route = MrXGraph.Home::class) {
        composable<HomeGraph.Home> {
            HomeScreen()
        }
        composable<HomeGraph.CreateGame> {
            CreateGameScreen()
        }
        composable<HomeGraph.JoinGame> {
            JoinGameScreen()
        }
        composable<HomeGraph.Tutorial> {
            TutorialScreen()
        }
    }
}

sealed interface HomeGraph {

    @Serializable
    data object Home : HomeGraph

    @Serializable
    data object CreateGame : HomeGraph

    @Serializable
    data object JoinGame : HomeGraph

    @Serializable
    data object Tutorial : HomeGraph

}
