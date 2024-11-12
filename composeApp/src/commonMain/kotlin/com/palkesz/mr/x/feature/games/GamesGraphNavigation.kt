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
import com.palkesz.mr.x.feature.games.question.barkochba.CreateBarkochbaQuestionScreen
import com.palkesz.mr.x.feature.games.question.barkochba.CreateBarkochbaQuestionViewModelImpl
import com.palkesz.mr.x.feature.games.question.create.CreateQuestionScreen
import com.palkesz.mr.x.feature.games.question.create.CreateQuestionViewModelImpl
import com.palkesz.mr.x.feature.games.question.guess.GuessQuestionScreen
import com.palkesz.mr.x.feature.games.question.guess.GuessQuestionViewModelImpl
import com.palkesz.mr.x.feature.games.question.specify.SpecifyQuestionScreen
import com.palkesz.mr.x.feature.games.question.specify.SpecifyQuestionViewModelImpl
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
        composable<GameGraph.CreateQuestion> { backStackEntry ->
            val createQuestion = backStackEntry.toRoute<GameGraph.CreateQuestion>()
            CreateQuestionScreen(viewModel = koinViewModel<CreateQuestionViewModelImpl>(parameters = {
                parameterSetOf(createQuestion.gameId)
            }))
        }
        composable<GameGraph.GuessQuestion> { backStackEntry ->
            val guessQuestion = backStackEntry.toRoute<GameGraph.GuessQuestion>()
            GuessQuestionScreen(viewModel = koinViewModel<GuessQuestionViewModelImpl>(parameters = {
                parameterSetOf(guessQuestion.gameId, guessQuestion.questionId)
            }))
        }
        composable<GameGraph.SpecifyQuestion> { backStackEntry ->
            val specifyQuestion = backStackEntry.toRoute<GameGraph.SpecifyQuestion>()
            SpecifyQuestionScreen(viewModel = koinViewModel<SpecifyQuestionViewModelImpl>(parameters = {
                parameterSetOf(specifyQuestion.gameId, specifyQuestion.questionId)
            }))
        }
        composable<GameGraph.CreateBarkochbaQuestion> { backStackEntry ->
            val createQuestion = backStackEntry.toRoute<GameGraph.CreateBarkochbaQuestion>()
            CreateBarkochbaQuestionScreen(
                viewModel = koinViewModel<CreateBarkochbaQuestionViewModelImpl>(parameters = {
                    parameterSetOf(createQuestion.gameId)
                })
            )
        }
    }
}

sealed interface GameGraph {

    @Serializable
    data object Games : GameGraph

    @Serializable
    data class Game(val id: String) : GameGraph

    @Serializable
    data class QrCode(val id: String) : GameGraph

    @Serializable
    data class CreateQuestion(val gameId: String) : GameGraph

    @Serializable
    data class GuessQuestion(val gameId: String, val questionId: String) : GameGraph

    @Serializable
    data class SpecifyQuestion(val gameId: String, val questionId: String) : GameGraph

    @Serializable
    data class CreateBarkochbaQuestion(val gameId: String) : GameGraph

}
