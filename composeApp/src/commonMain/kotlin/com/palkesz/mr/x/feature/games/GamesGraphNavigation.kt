package com.palkesz.mr.x.feature.games

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.palkesz.mr.x.feature.app.MrXGraph
import com.palkesz.mr.x.feature.games.game.GameScreen
import com.palkesz.mr.x.feature.games.game.GameViewModelImpl
import com.palkesz.mr.x.feature.games.qrcode.QrCodeScreen
import com.palkesz.mr.x.feature.games.qrcode.QrCodeViewModelImpl
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parameterSetOf

fun NavGraphBuilder.gamesGraphNavigation() {
    navigation(startDestination = GameGraph.Games, route = MrXGraph.Games::class) {
        composable<GameGraph.Games> {
            GamesScreen()
        }
        composable<GameGraph.Game> { backStackEntry ->
            val game = backStackEntry.toRoute<GameGraph.Game>()
            GameScreen(viewModel = koinViewModel<GameViewModelImpl>(parameters = {
                parameterSetOf(game.id)
            }))
        }
        composable<GameGraph.QrCode> { backStackEntry ->
            val qrCode = backStackEntry.toRoute<GameGraph.QrCode>()
            QrCodeScreen(viewModel = koinViewModel<QrCodeViewModelImpl>(parameters = {
                parameterSetOf(qrCode.id)
            }))
        }
        /*composable(
            route = GameGraphRoute.ChooseQuestion.route.plus("/{$GAME_ID}"),
            arguments =
            listOf(
                navArgument(
                    name = GAME_ID,
                    builder = {
                        type = NavType.StringType
                    },
                ),
            ),
        ) { backStackEntry ->
            ChooseQuestionScreen(gameId = backStackEntry.arguments?.getString(GAME_ID).toString())
        }
        composable(
            route = GameGraphRoute.NormalQuestion.route.plus("/{$GAME_ID}"),
            arguments =
            listOf(
                navArgument(
                    name = GAME_ID,
                    builder = {
                        type = NavType.StringType
                    },
                ),
            ),
        ) { backStackEntry ->
            NormalQuestionScreen(gameId = backStackEntry.arguments?.getString(GAME_ID).toString())
        }
        composable(
            route = GameGraphRoute.BarkochbaQuestion.route.plus("/{$GAME_ID}"),
            arguments =
            listOf(
                navArgument(
                    name = GAME_ID,
                    builder = {
                        type = NavType.StringType
                    },
                ),
            ),
        ) { backStackEntry ->
            BarkochbaQuestionScreen(
                gameId = backStackEntry.arguments?.getString(GAME_ID).toString(),
            )
        }
        composable(
            route =
            GameGraphRoute.SpecifyQuestion.route
                .plus("/{$QUESTION_ID}/{$GAME_ID}"),
            arguments =
            listOf(
                navArgument(
                    name = QUESTION_ID,
                    builder = {
                        type = NavType.StringType
                    },
                ),
                navArgument(
                    name = GAME_ID,
                    builder = {
                        type = NavType.StringType
                    },
                ),
            ),
        ) { backStackEntry ->
            SpecifyQuestionScreen(
                questionId = backStackEntry.arguments?.getString(QUESTION_ID).toString(),
                gameId = backStackEntry.arguments?.getString(GAME_ID).toString(),
            )
        }
        composable(
            route =
            GameGraphRoute.AnswerQuestion.route
                .plus("/{$QUESTION_ID}/{$GAME_ID}/{$IS_HOST}"),
            arguments =
            listOf(
                navArgument(
                    name = QUESTION_ID,
                    builder = {
                        type = NavType.StringType
                    },
                ),
                navArgument(
                    name = GAME_ID,
                    builder = {
                        type = NavType.StringType
                    },
                ),
                navArgument(
                    name = IS_HOST,
                    builder = {
                        type = NavType.BoolType
                    },
                ),
            ),
        ) { backStackEntry ->
            AnswerQuestionScreen(
                questionId = backStackEntry.arguments?.getString(QUESTION_ID).toString(),
                gameId = backStackEntry.arguments?.getString(GAME_ID).toString(),
                isHost = backStackEntry.arguments?.getBoolean(IS_HOST) ?: false,
            )
        }
        */
    }
}

sealed interface GameGraph {

    @Serializable
    data class Games(val newGameId: String? = null) : GameGraph

    @Serializable
    data class Game(val id: String) : GameGraph

    @Serializable
    data class QrCode(val id: String) : GameGraph

    /* data object ChooseQuestion : GameGraphRoute("CHOOSE_QUESTION") {
        fun createRoute(gameId: String) = "$route/$gameId"
    }

    data object NormalQuestion : GameGraphRoute("NORMAL_QUESTION") {
        fun createRoute(gameId: String) = "$route/$gameId"
    }

    data object BarkochbaQuestion : GameGraphRoute("BARKOCHBA_QUESTION") {
        fun createRoute(gameId: String) = "$route/$gameId"
    }

    data object SpecifyQuestion : GameGraphRoute("SPECIFY_QUESTION") {
        fun createRoute(
            questionId: String,
            gameId: String,
        ) = "$route/$questionId/$gameId"
    }

    data object AnswerQuestion : GameGraphRoute("ANSWER_QUESTION") {
        fun createRoute(
            questionId: String,
            gameId: String,
            isHost: Boolean,
        ) = "$route/$questionId/$gameId/$isHost"
    }*/
}
