package com.palkesz.mr.x.feature.games

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.palkesz.mr.x.feature.app.MrXGraph
import com.palkesz.mr.x.feature.games.answer.AnswerQuestionScreen
import com.palkesz.mr.x.feature.games.gameDetailsScreen.GameDetailsScreen
import com.palkesz.mr.x.feature.games.question.barkochba.BarkochbaQuestionScreen
import com.palkesz.mr.x.feature.games.question.choose.ChooseQuestionScreen
import com.palkesz.mr.x.feature.games.question.normal.NormalQuestionScreen
import com.palkesz.mr.x.feature.games.question.specify.SpecifyQuestionScreen
import com.palkesz.mr.x.feature.games.showQrCode.ShowQrCodeScreen

fun NavGraphBuilder.gamesGraphNavigation() {
    navigation(startDestination = GameGraphRoute.Games.route, route = MrXGraph.GAMES) {
        composable(route = GameGraphRoute.Games.route) {
            GamesScreen()
        }
        composable(
            route = GameGraphRoute.Game.route.plus("/{$GAME_ID}"),
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
            GameDetailsScreen(gameId = backStackEntry.arguments?.getString(GAME_ID).toString())
        }
        composable(
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
        composable(
            route =
            GameGraphRoute.GameQRCode.route
                .plus("/{$GAME_ID}"),
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
            ShowQrCodeScreen(gameId = backStackEntry.arguments?.getString(GAME_ID).toString())
        }
    }
}

sealed class GameGraphRoute(val route: String) {

    data object Games : GameGraphRoute("GAMES")

    data object Game : GameGraphRoute("GAME") {
        fun createRoute(gameId: String) = "$route/$gameId"
    }

    data object GameQRCode : GameGraphRoute("SHOW_QR_CODE") {
        fun createRoute(gameId: String) = "$route/$gameId"
    }

    data object ChooseQuestion : GameGraphRoute("CHOOSE_QUESTION") {
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
    }
}

const val GAME_ID = "gameId"
const val QUESTION_ID = "questionId"
const val IS_HOST = "isHost"
