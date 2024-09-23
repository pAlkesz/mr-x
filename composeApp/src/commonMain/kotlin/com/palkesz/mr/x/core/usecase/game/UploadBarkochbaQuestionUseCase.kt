package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.util.coroutines.CoroutineHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UploadBarkochbaQuestionUseCase(
	private val questionRepository: QuestionRepository,
	private val authRepository: AuthRepository
) {
	fun run(gameId: String, text: String) {
		authRepository.currentUserId?.let { userId ->
			CoroutineHelper.ioScope.launch {
				questionRepository.uploadBarkochbaQuestion(
					gameId = gameId,
					text = text.trim(),
					askerId = userId
				).collect()
			}
		}
	}
}
