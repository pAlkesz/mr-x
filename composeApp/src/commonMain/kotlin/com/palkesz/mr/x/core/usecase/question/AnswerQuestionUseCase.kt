package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository

class AnswerQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository
) {
    fun run(questionId: String, firstName: String, lastName: String, isHost: Boolean) {
        /*safeLet(
            p1 = authRepository.userId,
            p2 = questionRepository.playerQuestions.value.find { question ->
                question.uuid == questionId
            }) { userId, question ->
            CoroutineHelper.ioScope.launch {
                if (isHost) {
                    val status =
                        if (question.expectedFirstName.trimmedEqualsIgnoreCase(firstName) &&
                            question.expectedLastName.trimmedEqualsIgnoreCase(lastName)
                        ) {
                            Status.GUESSED_BY_HOST
                        } else {
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
                } else {
                    val status =
                        if (question.expectedFirstName.trimmedEqualsIgnoreCase(firstName) &&
                            question.expectedLastName.trimmedEqualsIgnoreCase(lastName)
                        ) {
                            Status.CORRECT_ANSWER
                        } else {
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
        }*/
    }
}
