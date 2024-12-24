package com.palkesz.mr.x.core.data.question

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.BarkochbaQuestion
import com.palkesz.mr.x.core.model.question.BarkochbaStatus
import com.palkesz.mr.x.core.util.extensions.map
import dev.gitlive.firebase.firestore.ChangeType
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Query
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface BarkochbaQuestionRepository {
    val questions: StateFlow<List<BarkochbaQuestion>>

    suspend fun fetchQuestions(gameId: String): Result<List<BarkochbaQuestion>>
    suspend fun fetchQuestions(gameIds: List<String>): Result<List<BarkochbaQuestion>>
    suspend fun createQuestion(question: BarkochbaQuestion): Result<Unit>
    suspend fun updateText(id: String, text: String): Result<String>
    suspend fun updateAnswer(id: String, answer: Boolean): Result<Unit>
    suspend fun observeQuestions()

    interface Stub : BarkochbaQuestionRepository {
        override val questions: StateFlow<List<BarkochbaQuestion>>
            get() = throw NotImplementedError()

        override suspend fun fetchQuestions(gameId: String): Result<List<BarkochbaQuestion>> =
            throw NotImplementedError()

        override suspend fun fetchQuestions(gameIds: List<String>): Result<List<BarkochbaQuestion>> =
            throw NotImplementedError()

        override suspend fun createQuestion(question: BarkochbaQuestion): Result<Unit> =
            throw NotImplementedError()

        override suspend fun updateText(id: String, text: String): Result<String> =
            throw NotImplementedError()

        override suspend fun updateAnswer(id: String, answer: Boolean): Result<Unit> =
            throw NotImplementedError()

        override suspend fun observeQuestions(): Unit = throw NotImplementedError()
    }
}

class BarkochbaQuestionRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val gameRepository: GameRepository,
) : BarkochbaQuestionRepository {

    private val _questions = MutableStateFlow<List<BarkochbaQuestion>>(emptyList())
    override val questions = _questions.asStateFlow()

    override suspend fun fetchQuestions(gameId: String): Result<List<BarkochbaQuestion>> {
        val gameQuestions = questions.value.filter { it.gameId == gameId }
        if (gameQuestions.isNotEmpty()) {
            return Result.success(gameQuestions)
        }
        return try {
            val questions = firestore.collection(BARKOCHBA_COLLECTION_NAME).where {
                GAME_ID_FIELD_KEY equalTo gameId
            }.processQuestionQuery()
            Result.success(questions)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
        }
    }

    override suspend fun fetchQuestions(gameIds: List<String>): Result<List<BarkochbaQuestion>> {
        val gameQuestions = questions.value.filter { it.gameId in gameIds }
        if (gameQuestions.map { it.gameId }.containsAll(elements = gameIds)) {
            return Result.success(gameQuestions)
        }
        return try {
            val questions = firestore.collection(BARKOCHBA_COLLECTION_NAME).where {
                GAME_ID_FIELD_KEY inArray gameIds
            }.processQuestionQuery()
            Result.success(questions)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
        }
    }

    override suspend fun createQuestion(question: BarkochbaQuestion) = try {
        firestore.collection(BARKOCHBA_COLLECTION_NAME).document(question.id).set(question)
        Result.success(Unit)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun updateText(id: String, text: String) = try {
        firestore.collection(BARKOCHBA_COLLECTION_NAME).document(id).update(
            Pair(TEXT_FIELD_KEY, text),
            Pair(STATUS_FIELD_KEY, BarkochbaStatus.ASKED),
            Pair(LAST_MODIFIED_FIELD_KEY, Timestamp.now())
        )
        Result.success(value = id)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun updateAnswer(id: String, answer: Boolean) = try {
        firestore.collection(BARKOCHBA_COLLECTION_NAME).document(id).update(
            Pair(ANSWER_FIELD_KEY, answer),
            Pair(STATUS_FIELD_KEY, BarkochbaStatus.ANSWERED),
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
            }.map { it.document.data(BarkochbaQuestion.serializer()) }
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
            firestore.collection(BARKOCHBA_COLLECTION_NAME).where {
                GAME_ID_FIELD_KEY inArray activeGames.map { it.id }
            }.orderBy(
                field = LAST_MODIFIED_FIELD_KEY,
                direction = Direction.DESCENDING,
            ).limit(QUESTIONS_FETCH_LIMIT).snapshots
        }
    }

    private suspend fun Query.processQuestionQuery(): List<BarkochbaQuestion> {
        val questions = orderBy(
            field = LAST_MODIFIED_FIELD_KEY,
            direction = Direction.DESCENDING,
        ).limit(QUESTIONS_FETCH_LIMIT).get().documents.map {
            it.data(BarkochbaQuestion.serializer())
        }
        _questions.update { cachedQuestions ->
            (questions + cachedQuestions).distinctBy { it.id }
                .sortedByDescending { it.lastModifiedTimestamp.seconds }
        }
        return questions
    }

    companion object {
        private const val BARKOCHBA_COLLECTION_NAME = "barkochba"
        private const val GAME_ID_FIELD_KEY = "gameId"
        private const val ANSWER_FIELD_KEY = "answer"
        private const val TEXT_FIELD_KEY = "text"
        private const val STATUS_FIELD_KEY = "status"
        private const val LAST_MODIFIED_FIELD_KEY = "lastModifiedTimestamp"
        private const val QUESTIONS_FETCH_LIMIT = 300
    }
}
