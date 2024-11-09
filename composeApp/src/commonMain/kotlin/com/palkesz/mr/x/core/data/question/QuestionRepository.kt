package com.palkesz.mr.x.core.data.question

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.Answer
import com.palkesz.mr.x.core.model.question.Question
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.util.extensions.map
import dev.gitlive.firebase.firestore.ChangeType
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface QuestionRepository {
    val questions: StateFlow<List<Question>>

    suspend fun fetchQuestions(gameId: String): Result<List<Question>>
    suspend fun fetchQuestion(id: String): Result<Question>
    suspend fun createQuestion(question: Question): Result<Unit>
    suspend fun updateHostAnswer(id: String, answer: Answer, status: QuestionStatus): Result<Unit>
    suspend fun uploadPlayerAnswer(id: String, answer: Answer, status: QuestionStatus): Result<Unit>
    suspend fun updateStatus(id: String, status: QuestionStatus): Result<Unit>
    suspend fun updateText(id: String, text: String): Result<Unit>
    suspend fun observeQuestions()
}

class QuestionRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val gameRepository: GameRepository,
) : QuestionRepository {

    private val _questions = MutableStateFlow(emptyList<Question>())
    override val questions = _questions.asStateFlow()

    override suspend fun fetchQuestions(gameId: String): Result<List<Question>> {
        val gameQuestions = questions.value.filter { it.gameId == gameId }
        if (gameQuestions.isNotEmpty()) {
            return Result.success(gameQuestions)
        }
        return try {
            val questions = firestore.collection(QUESTIONS_COLLECTION_NAME).where {
                GAME_ID_FIELD_KEY equalTo gameId
            }.orderBy(
                field = LAST_MODIFIED_FIELD_KEY,
                direction = Direction.DESCENDING,
            ).limit(QUESTIONS_FETCH_LIMIT).get().documents.map {
                it.data(Question.serializer())
            }
            _questions.update { cachedQuestions ->
                (questions + cachedQuestions).distinctBy { it.id }
                    .sortedByDescending { it.lastModifiedTimestamp.seconds }
            }
            Result.success(questions)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
        }
    }

    override suspend fun fetchQuestion(id: String): Result<Question> {
        questions.value.find { it.id == id }?.let {
            return Result.success(it)
        }
        return try {
            val question = firestore.collection(QUESTIONS_COLLECTION_NAME).document(id).get()
                .data(Question.serializer())
            _questions.update { cachedQuestions ->
                (listOf(question) + cachedQuestions).distinctBy { it.id }
                    .sortedByDescending { it.lastModifiedTimestamp.seconds }
            }
            Result.success(question)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
        }
    }

    override suspend fun createQuestion(question: Question) = try {
        firestore.collection(QUESTIONS_COLLECTION_NAME).document(question.id).set(question)
        Result.success(Unit)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun updateHostAnswer(id: String, answer: Answer, status: QuestionStatus) =
        try {
            firestore.collection(QUESTIONS_COLLECTION_NAME).document(id).update(
                Pair(HOST_ANSWER_FIELD_KEY, answer),
                Pair(STATUS_FIELD_KEY, status),
                Pair(LAST_MODIFIED_FIELD_KEY, Timestamp.now())
            )
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
        }

    override suspend fun uploadPlayerAnswer(id: String, answer: Answer, status: QuestionStatus) =
        try {
            firestore.collection(QUESTIONS_COLLECTION_NAME).document(id).update(
                Pair(PLAYER_ANSWER_FIELD_KEY, answer),
                Pair(STATUS_FIELD_KEY, status),
                Pair(LAST_MODIFIED_FIELD_KEY, Timestamp.now())
            )
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
        }

    override suspend fun updateStatus(id: String, status: QuestionStatus) = try {
        firestore.collection(QUESTIONS_COLLECTION_NAME).document(id).update(
            Pair(STATUS_FIELD_KEY, status),
            Pair(LAST_MODIFIED_FIELD_KEY, Timestamp.now())
        )
        Result.success(Unit)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun updateText(id: String, text: String) = try {
        firestore.collection(QUESTIONS_COLLECTION_NAME).document(id).update(
            Pair(TEXT_FIELD_KEY, text),
            Pair(STATUS_FIELD_KEY, QuestionStatus.WAITING_FOR_HOST),
            Pair(LAST_MODIFIED_FIELD_KEY, Timestamp.now())
        )
        Result.success(Unit)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun observeQuestions() {
        getQuestionSnapshotFlow().map { snapshot ->
            snapshot.documentChanges.partition { change ->
                change.type == ChangeType.REMOVED
            }.map { it.document.data(Question.serializer()) }
        }.collect { (removedQuestions, addedOrModifiedQuestions) ->
            _questions.update { questions ->
                (addedOrModifiedQuestions + questions).distinctBy { question -> question.id }
                    .subtract(removedQuestions.toSet())
                    .sortedByDescending { it.lastModifiedTimestamp.seconds }.toList()
            }
        }
    }

    private fun getQuestionSnapshotFlow() = gameRepository.games.flatMapLatest { games ->
        val activeGames = games.filter { it.status == GameStatus.ONGOING }
        if (activeGames.isEmpty()) {
            flowOf()
        } else {
            firestore.collection(QUESTIONS_COLLECTION_NAME).where {
                GAME_ID_FIELD_KEY inArray activeGames.map { it.id }
            }.orderBy(
                field = LAST_MODIFIED_FIELD_KEY,
                direction = Direction.DESCENDING,
            ).limit(QUESTIONS_FETCH_LIMIT).snapshots
        }
    }

    companion object {
        private const val QUESTIONS_COLLECTION_NAME = "questions"
        private const val GAME_ID_FIELD_KEY = "gameId"
        private const val HOST_ANSWER_FIELD_KEY = "hostAnswer"
        private const val PLAYER_ANSWER_FIELD_KEY = "playerAnswer"
        private const val TEXT_FIELD_KEY = "text"
        private const val STATUS_FIELD_KEY = "status"
        private const val LAST_MODIFIED_FIELD_KEY = "lastModifiedTimestamp"
        private const val QUESTIONS_FETCH_LIMIT = 300
    }
}
