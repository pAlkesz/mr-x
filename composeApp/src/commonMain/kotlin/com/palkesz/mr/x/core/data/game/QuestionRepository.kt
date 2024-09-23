package com.palkesz.mr.x.core.data.game

import com.palkesz.mr.x.core.model.game.Answer
import com.palkesz.mr.x.core.model.game.Question
import com.palkesz.mr.x.core.model.game.Status
import com.palkesz.mr.x.core.util.*
import com.palkesz.mr.x.core.util.network.NetworkErrorRepository
import dev.gitlive.firebase.firestore.ChangeType
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.normal_question_not_found_error
import org.jetbrains.compose.resources.getString

interface QuestionRepository {

	val playerQuestions: StateFlow<List<Question>>

	fun getQuestionsOfGame(gameId: String): Flow<Result<List<Question>>>
	fun uploadQuestion(question: Question): Flow<Result<Unit>>
	fun uploadBarkochbaQuestion(askerId: String, gameId: String, text: String): Flow<Result<Unit>>
	fun uploadHostAnswer(uuid: String, answer: Answer, status: Status): Flow<Result<Unit>>
	fun uploadPlayerAnswer(uuid: String, answer: Answer, status: Status): Flow<Result<Unit>>
	fun updateStatus(uuid: String, status: Status): Flow<Result<Unit>>
	fun updateQuestionText(uuid: String, text: String): Flow<Result<Unit>>
	fun getQuestion(uuid: String): Flow<Result<Question>>
	fun updateBarkochbaQuestion(
		answer: Boolean,
		questionId: String,
		status: Status): Flow<Result<Unit>>
}

class QuestionRepositoryImpl(
	private val firestore: FirebaseFirestore,
	private val gameRepository: GameRepository,
	private val networkErrorRepository: NetworkErrorRepository
) : QuestionRepository {

	private val _playerQuestions = MutableStateFlow(emptyList<Question>())
	override val playerQuestions = _playerQuestions.asStateFlow()

	init {
		observePlayerQuestionChanges()
	}

	override fun getQuestionsOfGame(gameId: String) = flow<Result<List<Question>>> {
		if (_playerQuestions.value.isNotEmpty()) {
			emit(Success(_playerQuestions.value.filter { it.gameId == gameId }))
			return@flow
		}
		emit(Loading())
		try {
			val questions = firestore.collection(COLLECTION_NAME).where {
				GAME_FIELD equalTo gameId
			}.get().documents.map {
				it.data(Question.serializer())
			}
			emit(Success(questions))
		}
		catch (e: Exception) {
			emit(Error(e))
		}
	}

	override fun uploadQuestion(question: Question) = flow<Result<Unit>> {
		emit(Loading())
		try {
			firestore.collection(COLLECTION_NAME).document(question.uuid).set(question)
			emit(Success(Unit))
		}
		catch (e: Exception) {
			networkErrorRepository.addError(e)
			emit(Error(e))
		}
	}

	override fun uploadBarkochbaQuestion(askerId: String, gameId: String, text: String) =
		flow<Result<Unit>> {
			emit(Loading())
			try {
				val questionId = _playerQuestions.value.find {
					it.gameId == gameId && it.askerId == askerId && it.status == Status.CORRECT_ANSWER
				}?.uuid
				if (questionId == null) {
					emit(Error(IllegalStateException(getString(Res.string.normal_question_not_found_error))))
				}
				else {
					firestore.collection(COLLECTION_NAME).document(questionId).update(
						Pair(STATUS_FIELD, Status.BARKOCHBA_ASKED),
						Pair(BARKOCHBA_TEXT_FIELD, text),
						Pair(TIMESTAMP_FIELD, Timestamp.now()),
					)
					emit(Success(Unit))
				}
			}
			catch (e: Exception) {
				networkErrorRepository.addError(e)
				emit(Error(e))
			}
		}

	override fun uploadHostAnswer(uuid: String, answer: Answer, status: Status) =
		flow<Result<Unit>> {
			emit(Loading())
			try {
				firestore.collection(COLLECTION_NAME).document(uuid).update(
					Pair(HOST_ANSWER_FIELD, answer),
					Pair(STATUS_FIELD, status),
					Pair(TIMESTAMP_FIELD, Timestamp.now())
				)
				emit(Success(Unit))
			}
			catch (e: Exception) {
				networkErrorRepository.addError(e)
				emit(Error(e))
			}
		}

	override fun uploadPlayerAnswer(uuid: String, answer: Answer, status: Status) =
		flow<Result<Unit>> {
			emit(Loading())
			try {
				firestore.collection(COLLECTION_NAME).document(uuid).update(
					Pair(PLAYER_ANSWER_FIELD, answer),
					Pair(STATUS_FIELD, status),
					Pair(TIMESTAMP_FIELD, Timestamp.now())
				)
				emit(Success(Unit))
			}
			catch (e: Exception) {
				networkErrorRepository.addError(e)
				emit(Error(e))
			}
		}

	override fun updateStatus(uuid: String, status: Status) = flow<Result<Unit>> {
		emit(Loading())
		try {
			firestore.collection(COLLECTION_NAME).document(uuid).update(
				Pair(STATUS_FIELD, status),
				Pair(TIMESTAMP_FIELD, Timestamp.now())
			)
			emit(Success(Unit))
		}
		catch (e: Exception) {
			networkErrorRepository.addError(e)
			emit(Error(e))
		}
	}

	override fun updateQuestionText(uuid: String, text: String) = flow<Result<Unit>> {
		emit(Loading())
		try {
			firestore.collection(COLLECTION_NAME).document(uuid).update(
				Pair(QUESTION_TEXT_FIELD, text),
				Pair(STATUS_FIELD, Status.WAITING_FOR_HOST),
				Pair(TIMESTAMP_FIELD, Timestamp.now())
			)
			emit(Success(Unit))
		}
		catch (e: Exception) {
			networkErrorRepository.addError(e)
			emit(Error(e))
		}
	}

	override fun getQuestion(uuid: String) = flow<Result<Question>> {
		_playerQuestions.value.find { it.uuid == uuid }?.let {
			emit(Success(it))
			return@flow
		}
		emit(Loading())
		try {
			val res = firestore.collection(COLLECTION_NAME).document(uuid).get()
				.data(Question.serializer())
			emit(Success(res))
		}
		catch (e: Exception) {
			emit(Error(e))
		}
	}

	override fun updateBarkochbaQuestion(answer: Boolean, questionId: String, status: Status) =
		flow<Result<Unit>> {
			emit(Loading())
			try {
				firestore.collection(COLLECTION_NAME).document(questionId).update(
					Pair(BARKOCHBA_ANSWER_FIELD, answer),
					Pair(STATUS_FIELD, status),
					Pair(TIMESTAMP_FIELD, Timestamp.now())
				)
				emit(Success(Unit))
			}
			catch (e: Exception) {
				networkErrorRepository.addError(e)
				emit(Error(e))
			}
		}

	private fun observePlayerQuestionChanges() {
		CoroutineHelper.ioScope.launch {
			try {
				playerQuestionsQuerySnapshots().collect { snapshot ->
					val (removed, addedOrModified) =
						snapshot.documentChanges.partition { change ->
							change.type == ChangeType.REMOVED
						}
					_playerQuestions.update { questions ->
						(addedOrModified.map {
							it.document.data(Question.serializer())
						} + questions).distinctBy(Question::uuid)
							.subtract(removed.map {
								it.document.data(Question.serializer())
							}.toSet()).toList()
					}
				}
			}
			catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	private fun playerQuestionsQuerySnapshots() =
		gameRepository.playerGames.flatMapLatest { games ->
			if (games.isEmpty()) {
				flowOf()
			}
			else {
				firestore.collection(COLLECTION_NAME).where {
					GAME_FIELD inArray games.map { it.uuid }
				}.snapshots
			}
		}

	companion object {
		const val COLLECTION_NAME = "questions"
		const val STATUS_FIELD = "status"
		const val GAME_FIELD = "gameId"
		const val BARKOCHBA_TEXT_FIELD = "barkochbaText"
		const val TIMESTAMP_FIELD = "lastModifiedTS"
		const val HOST_ANSWER_FIELD = "hostAnswer"
		const val PLAYER_ANSWER_FIELD = "playerAnswer"
		const val QUESTION_TEXT_FIELD = "text"
		const val BARKOCHBA_ANSWER_FIELD = "barkochbaAnswer"
	}
}
