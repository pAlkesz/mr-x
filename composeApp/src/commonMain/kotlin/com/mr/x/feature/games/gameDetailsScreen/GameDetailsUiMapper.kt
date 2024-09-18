package com.mr.x.feature.games.gameDetailsScreen

import com.mr.x.core.model.GameWithHost
import com.mr.x.core.model.game.GameStatus
import com.mr.x.core.model.game.Question
import com.mr.x.core.model.game.Status
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class GameDetailsUiMapper {
	fun mapViewState(
		pair: Pair<GameWithHost, List<Question>>,
		userId: String?,
		filters: ImmutableList<QuestionItemFilter>
	) = if (pair.first.host.userId == userId) {
		GameDetailsViewState(
			hostName = pair.first.host.userName,
			defaultTitle = pair.first.game.lastName.first().toString(),
			optionalHiddenTitle = "${pair.first.game.firstName} ${pair.first.game.lastName}".trim(),
			playerIsHost = true,
			isAskQuestionButtonVisible = false,
			gameStatus = pair.first.game.status,
			questions = mapQuestions(
				questions = pair.second,
				playerIsHost = true,
				userId = userId,
				filters = filters),
			selectedFilters = filters
		)
	}
	else {
		GameDetailsViewState(
			hostName = pair.first.host.userName,
			defaultTitle = pair.first.game.lastName.first().toString(),
			optionalHiddenTitle = null,
			playerIsHost = false,
			isAskQuestionButtonVisible = pair.first.game.status == GameStatus.ONGOING,
			gameStatus = pair.first.game.status,
			questions = mapQuestions(
				questions = pair.second,
				playerIsHost = false,
				userId = userId,
				filters = filters),
			selectedFilters = filters
		)
	}

	fun mapQuestions(
		questions: List<Question>,
		playerIsHost: Boolean,
		userId: String?,
		filters: ImmutableList<QuestionItemFilter>
	) = questions.sortedByDescending {
		it.lastModifiedTS.seconds
	}.mapNotNull { question ->
		when {
			playerIsHost && question.status == Status.WAITING_FOR_HOST
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.PLAYER_TURN)) -> {
				HostAnswersQuestionItem(
					text = question.text,
					uuid = question.uuid,
					gameId = question.gameId
				)
			}
			playerIsHost && question.status == Status.WAITING_FOR_PLAYERS
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.WAITING_FOR_OTHERS)) -> {
				HostWaitsForPlayersQuestionItem(
					text = question.text,
					uuid = question.uuid
				)
			}
			playerIsHost && question.status == Status.WAITING_FOR_OWNER
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.WAITING_FOR_OTHERS)) -> {
				HostWaitsForOwnerQuestionItem(
					text = question.text,
					uuid = question.uuid,
					hostAnswer = question.hostAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					}
				)
			}
			question.status == Status.WRONG_ANSWER
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.WRONG_ANSWER)) -> {
				WrongAnswerQuestionItem(
					text = question.text,
					uuid = question.uuid,
					hostAnswer = question.hostAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					},
					playerAnswer = question.playerAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					},
					expectedAnswer = "${question.expectedFirstName} ${
						question.expectedLastName
					}".trim()
				)
			}
			playerIsHost && question.status == Status.BARKOCHBA_ASKED
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.BARKOCHBA)
					|| filters.contains(QuestionItemFilter.CORRECT_ANSWER)) -> {
				HostSeesBarkochbaQuestionItem(
					text = question.text,
					uuid = question.uuid,
					playerAnswer = question.playerAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					},
					barkochbaText = question.barkochbaText
				)
			}
			question.status == Status.BARKOCHBA_ANSWERED
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.BARKOCHBA)
					|| filters.contains(QuestionItemFilter.CORRECT_ANSWER)) -> {
				question.barkochbaAnswer?.let { answer ->
					BarkochbaAnsweredQuestionItem(
						text = question.text,
						uuid = question.uuid,
						hostAnswer = question.hostAnswer?.let {
							"${it.firstName} ${it.lastName}".trim()
						},
						correctAnswer = "${question.expectedFirstName} ${
							question.expectedLastName
						}".trim(),
						barkochbaText = question.barkochbaText,
						barkochbaAnswer = answer
					)
				}
			}
			!playerIsHost && question.status == Status.WAITING_FOR_HOST
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.WAITING_FOR_OTHERS)) -> {
				PlayerWaitsForHostQuestionItem(
					text = question.text,
					uuid = question.uuid
				)
			}
			!playerIsHost && question.status == Status.WAITING_FOR_PLAYERS
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.WAITING_FOR_OTHERS))
					&& question.askerId == userId -> {
				QuestionOwnerWaitsForPlayers(
					text = question.text,
					uuid = question.uuid
				)
			}
			!playerIsHost && question.status == Status.WAITING_FOR_PLAYERS
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.PLAYER_TURN))
					&& question.askerId != userId -> {
				PlayerCanAnswerQuestionItem(
					text = question.text,
					uuid = question.uuid,
					gameId = question.gameId
				)
			}
			!playerIsHost && question.status == Status.WAITING_FOR_OWNER
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.PLAYER_TURN))
					&& question.askerId == userId -> {
				OwnerEvaluatesQuestionItem(
					text = question.text,
					uuid = question.uuid,
					gameId = question.gameId,
					hostAnswer = question.hostAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					}
				)
			}
			!playerIsHost && question.status == Status.WAITING_FOR_OWNER
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.WAITING_FOR_OTHERS))
					&& question.askerId != userId -> {
				PlayerWaitsForOwnerQuestionItem(
					text = question.text,
					uuid = question.uuid,
					hostAnswer = question.hostAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					}
				)
			}
			!playerIsHost && question.status == Status.BARKOCHBA_ASKED
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.BARKOCHBA)
					|| filters.contains(QuestionItemFilter.CORRECT_ANSWER)) -> {
				PlayerSeesBarkochbaQuestionItem(
					text = question.text,
					uuid = question.uuid,
					hostAnswer = question.hostAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					},
					playerAnswer = question.playerAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					},
					barkochbaText = question.barkochbaText
				)
			}
			question.status == Status.CORRECT_ANSWER
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.CORRECT_ANSWER)) -> {
				CorrectAnswerQuestionItem(
					text = question.text,
					uuid = question.uuid,
					hostAnswer = question.hostAnswer?.let {
						"${it.firstName} ${it.lastName}".trim()
					},
					correctAnswer = "${question.expectedFirstName} ${
						question.expectedLastName
					}".trim(),
					isHost = playerIsHost
				)
			}
			question.status == Status.GUESSED_BY_HOST
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.GUESSED_BY_HOST)) -> {
				GuessedByHostQuestionItem(
					text = question.text,
					uuid = question.uuid,
					hostAnswer = "${question.hostAnswer?.firstName} ${
						question.hostAnswer?.lastName
					}".trim(),
					isHost = playerIsHost
				)
			}
			question.status == Status.PLAYER_WON
					&& (filters.isEmpty() || filters.contains(QuestionItemFilter.WON)) -> {
				PlayerWonQuestionItem(
					text = question.text,
					uuid = question.uuid,
					answer = "${question.expectedFirstName} ${
						question.expectedLastName
					}".trim()
				)
			}
			else -> {
				null
			}
		}
	}.toPersistentList()
}
