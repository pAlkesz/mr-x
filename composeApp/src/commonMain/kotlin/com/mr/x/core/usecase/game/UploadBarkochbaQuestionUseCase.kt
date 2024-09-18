package com.mr.x.core.usecase.game

import com.mr.x.core.data.game.QuestionRepository
import com.mr.x.core.data.user.AuthRepository
import com.mr.x.core.util.CoroutineHelper
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
