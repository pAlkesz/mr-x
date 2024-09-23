package com.palkesz.mr.x.core.usecase.game.question

import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.model.game.Answer
import com.palkesz.mr.x.core.model.game.Status
import com.palkesz.mr.x.core.util.CoroutineHelper
import com.palkesz.mr.x.core.util.safeLet
import com.palkesz.mr.x.core.util.trimmedEqualsIgnoreCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AnswerQuestionUseCase(
	private val questionRepository: QuestionRepository,
	private val authRepository: AuthRepository
) {
	fun run(questionId: String, firstName: String, lastName: String, isHost: Boolean) {
		safeLet(
			p1 = authRepository.currentUserId,
			p2 = questionRepository.playerQuestions.value.find { question ->
				question.uuid == questionId
			}) { userId, question ->
			CoroutineHelper.ioScope.launch {
				if (isHost) {
					val status =
						if (question.expectedFirstName.trimmedEqualsIgnoreCase(firstName) &&
							question.expectedLastName.trimmedEqualsIgnoreCase(lastName)) {
							Status.GUESSED_BY_HOST
						}
						else {
							Status.WAITING_FOR_OWNER
						}
					questionRepository.uploadHostAnswer(
						uuid = questionId,
						answer = Answer(
							firstName = firstName.trim(),
							lastName = lastName.trim(),
							userId = userId
						),
						status = status
					).collect()
				}
				else {
					val status =
						if (question.expectedFirstName.trimmedEqualsIgnoreCase(firstName) &&
							question.expectedLastName.trimmedEqualsIgnoreCase(lastName)) {
							Status.CORRECT_ANSWER
						}
						else {
							Status.WRONG_ANSWER
						}
					questionRepository.uploadPlayerAnswer(
						uuid = questionId,
						answer = Answer(
							firstName = firstName.trim(),
							lastName = lastName.trim(),
							userId = userId
						),
						status = status
					).collect()
				}
			}
		}
	}
}
