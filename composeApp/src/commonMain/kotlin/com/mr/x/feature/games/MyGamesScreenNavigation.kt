package com.mr.x.feature.games

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.mr.x.feature.app.MrXGraph
import com.mr.x.feature.games.answer.AnswerQuestionScreen
import com.mr.x.feature.games.gameDetailsScreen.GameDetailsScreen
import com.mr.x.feature.games.question.barkochba.BarkochbaQuestionScreen
import com.mr.x.feature.games.question.choose.ChooseQuestionScreen
import com.mr.x.feature.games.question.normal.NormalQuestionScreen
import com.mr.x.feature.games.question.specify.SpecifyQuestionScreen
import com.mr.x.feature.games.showQrCode.ShowQrCodeScreen
import com.mr.x.feature.home.scanQrCode.ScanQrCodeScreen

fun NavGraphBuilder.myGamesScreenNavigation() {
	navigation(
		startDestination = GameScreenRoute.MyGamesPage.route,
		route = MrXGraph.GAMES
	) {
		composable(route = GameScreenRoute.MyGamesPage.route) {
			MyGamesScreen()
		}
		composable(route = GameScreenRoute.InGame.route.plus("/{$GAME_ID}"),
			arguments = listOf(navArgument(name = GAME_ID,
				builder = {
					type = NavType.StringType
				})))
		{ backStackEntry ->
			GameDetailsScreen(gameId = backStackEntry.arguments?.getString(GAME_ID).toString())
		}
		composable(route = GameScreenRoute.ChooseQuestion.route.plus("/{$GAME_ID}"),
			arguments = listOf(navArgument(name = GAME_ID,
				builder = {
					type = NavType.StringType
				})))
		{ backStackEntry ->
			ChooseQuestionScreen(gameId = backStackEntry.arguments?.getString(GAME_ID).toString())
		}
		composable(route = GameScreenRoute.NormalQuestion.route.plus("/{$GAME_ID}"),
			arguments = listOf(navArgument(name = GAME_ID,
				builder = {
					type = NavType.StringType
				})))
		{ backStackEntry ->
			NormalQuestionScreen(gameId = backStackEntry.arguments?.getString(GAME_ID).toString())
		}
		composable(route = GameScreenRoute.BarkochbaQuestion.route.plus("/{$GAME_ID}"),
			arguments = listOf(navArgument(name = GAME_ID,
				builder = {
					type = NavType.StringType
				})))
		{ backStackEntry ->
			BarkochbaQuestionScreen(
				gameId = backStackEntry.arguments?.getString(GAME_ID).toString()
			)
		}
		composable(route = GameScreenRoute.SpecifyQuestion.route
			.plus("/{$QUESTION_ID}/{$GAME_ID}"),
			arguments = listOf(
				navArgument(name = QUESTION_ID,
					builder = {
						type = NavType.StringType
					}),
				navArgument(name = GAME_ID,
					builder = {
						type = NavType.StringType
					})))
		{ backStackEntry ->
			SpecifyQuestionScreen(
				questionId = backStackEntry.arguments?.getString(QUESTION_ID).toString(),
				gameId = backStackEntry.arguments?.getString(GAME_ID).toString()
			)
		}
		composable(route = GameScreenRoute.AnswerQuestion.route
			.plus("/{$QUESTION_ID}/{$GAME_ID}/{$IS_HOST}"),
			arguments = listOf(
				navArgument(name = QUESTION_ID,
					builder = {
						type = NavType.StringType
					}),
				navArgument(name = GAME_ID,
					builder = {
						type = NavType.StringType
					}),
				navArgument(name = IS_HOST,
					builder = {
						type = NavType.BoolType
					})))
		{ backStackEntry ->
			AnswerQuestionScreen(
				questionId = backStackEntry.arguments?.getString(QUESTION_ID).toString(),
				gameId = backStackEntry.arguments?.getString(GAME_ID).toString(),
				isHost = backStackEntry.arguments?.getBoolean(IS_HOST) ?: false
			)
		}
		composable(route = GameScreenRoute.GameQRCode.route
			.plus("/{$GAME_ID}"),
			arguments = listOf(
				navArgument(name = GAME_ID,
					builder = {
						type = NavType.StringType
					}))) { backStackEntry ->
			ShowQrCodeScreen(gameId = backStackEntry.arguments?.getString(GAME_ID).toString())
		}
		composable(route = GameScreenRoute.ScanQrCode.route) {
			ScanQrCodeScreen()
		}
	}
}

sealed class GameScreenRoute(val route: String) {
	data object MyGamesPage : GameScreenRoute("MY_GAMES_PAGE")
	data object ScanQrCode : GameScreenRoute("SCAN_QR_CODE")
	data object InGame : GameScreenRoute("IN_GAME") {
		fun createRoute(gameId: String) = "$route/${gameId}"
	}

	data object GameQRCode : GameScreenRoute("SHOW_QR_CODE") {
		fun createRoute(gameId: String) = "$route/${gameId}"
	}

	data object ChooseQuestion : GameScreenRoute("CHOOSE_QUESTION") {
		fun createRoute(gameId: String) = "$route/${gameId}"
	}

	data object NormalQuestion : GameScreenRoute("NORMAL_QUESTION") {
		fun createRoute(gameId: String) = "$route/${gameId}"
	}

	data object BarkochbaQuestion : GameScreenRoute("BARKOCHBA_QUESTION") {
		fun createRoute(gameId: String) = "$route/${gameId}"
	}

	data object SpecifyQuestion : GameScreenRoute("SPECIFY_QUESTION") {
		fun createRoute(questionId: String, gameId: String) = "$route/${questionId}/${gameId}"
	}

	data object AnswerQuestion : GameScreenRoute("ANSWER_QUESTION") {
		fun createRoute(questionId: String, gameId: String, isHost: Boolean) =
			"$route/${questionId}/${gameId}/${isHost}"
	}
}

const val GAME_ID = "gameId"
const val QUESTION_ID = "questionId"
const val IS_HOST = "isHost"
