package com.palkesz.mr.x.feature.games.gameDetailsScreen

import com.palkesz.mr.x.core.usecase.game.AnswerBarkochbaQuestionUseCase
import com.palkesz.mr.x.core.usecase.game.GetGameWithHostUseCase
import com.palkesz.mr.x.core.usecase.game.GetQuestionsOfGameUseCase
import com.palkesz.mr.x.core.usecase.game.PassQuestionAsHostUseCase
import com.palkesz.mr.x.core.usecase.game.question.DeclineHostAnswerUseCase
import org.koin.dsl.module

val gameDetailsModule = module {
	factory { GetGameWithHostUseCase(get(), get()) }
	factory { GetQuestionsOfGameUseCase(get()) }
	factory { AnswerBarkochbaQuestionUseCase(get()) }
	factory { GameDetailsUiMapper() }
	factory { PassQuestionAsHostUseCase(get()) }
	factory { DeclineHostAnswerUseCase(get()) }
	includes(gameDetailsViewModelModule)
}
