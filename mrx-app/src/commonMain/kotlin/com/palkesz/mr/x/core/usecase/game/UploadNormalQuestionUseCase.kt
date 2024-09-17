package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.game.Question
import com.palkesz.mr.x.core.model.game.Status
import com.palkesz.mr.x.core.util.CoroutineHelper
import com.palkesz.mr.x.core.util.randomUUID
import com.palkesz.mr.x.core.util.trimmedEqualsIgnoreCase
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UploadNormalQuestionUseCase(
	private val questionRepository: QuestionRepository,
	private val authRepository: AuthRepository,
	private val gameRepository: GameRepository
) {

	fun run(text: String, firstName: String, lastName: String, gameId: String) {
		authRepository.currentUserId?.let { userId ->
			CoroutineHelper.ioScope.launch {
				gameRepository.playerGames.value.find {
					it.uuid == gameId
				}?.let { game ->
					val status = if (game.firstName.trimmedEqualsIgnoreCase(firstName) &&
						game.lastName.trimmedEqualsIgnoreCase(lastName)) {
						Status.PLAYER_WON
					}
					else {
						Status.WAITING_FOR_HOST
					}

					if (status == Status.PLAYER_WON) {
						gameRepository.updateStatus(
							gameId = game.uuid,
							status = GameStatus.FINISHED
						).collect()
					}

					questionRepository.uploadQuestion(
						question = Question(
							text = text.trim(),
							expectedFirstName = firstName.trim(),
							expectedLastName = lastName.trim(),
							uuid = randomUUID(),
							gameId = gameId,
							status = status,
							askerId = userId,
							lastModifiedTS = Timestamp.now()
						)
					).collect()
				}
			}
		}
	}
}
