package com.palkesz.mr.x.core.usecase.game.question

import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.model.game.Status
import com.palkesz.mr.x.core.util.coroutines.CoroutineHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DeclineHostAnswerUseCase(
    private val questionRepository: QuestionRepository
) {

    fun run(questionId: String) {
        CoroutineHelper.ioScope.launch {
            questionRepository.updateStatus(
                uuid = questionId,
                status = Status.WAITING_FOR_PLAYERS
            ).collect()
        }
    }
}
