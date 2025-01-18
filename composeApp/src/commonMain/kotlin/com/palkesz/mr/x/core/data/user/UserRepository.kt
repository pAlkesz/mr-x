package com.palkesz.mr.x.core.data.user

import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.model.user.User
import com.palkesz.mr.x.core.util.extensions.map
import dev.gitlive.firebase.firestore.ChangeType
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface UserRepository {
    val users: StateFlow<List<User>>

    suspend fun uploadUser(user: User): Result<User>
    suspend fun fetchUser(id: String): Result<User>
    suspend fun fetchUsers(ids: List<String>): Result<List<User>>
    suspend fun observeUsers()

    interface Stub : UserRepository {
        override val users: StateFlow<List<User>>
            get() = throw NotImplementedError()

        override suspend fun fetchUser(id: String): Result<User> = throw NotImplementedError()
        override suspend fun fetchUsers(ids: List<String>): Result<List<User>> =
            throw NotImplementedError()

        override suspend fun uploadUser(user: User): Result<User> = throw NotImplementedError()
        override suspend fun observeUsers(): Unit = throw NotImplementedError()
    }
}

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val questionRepository: QuestionRepository,
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
) : UserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users = _users.asStateFlow()

    override suspend fun uploadUser(user: User) = runCatching {
        firestore.collection(PLAYERS_COLLECTION_KEY).document(user.id).set(user)
        updateCachedUser(user = user)
        user
    }

    override suspend fun fetchUser(id: String) = runCatching {
        firestore.collection(PLAYERS_COLLECTION_KEY)
            .document(id)
            .get()
            .data(User.serializer())
            .also { user ->
                updateCachedUser(user = user)
            }
    }

    override suspend fun fetchUsers(ids: List<String>) = runCatching {
        if (users.value.map { it.id }.containsAll(elements = ids)) {
            return@runCatching users.value.filter { it.id in ids }
        }
        firestore.collection(PLAYERS_COLLECTION_KEY).where {
            ID_FIELD_KEY inArray ids
        }.get().documents.map { it.data(User.serializer()) }.also { users ->
            _users.update { cachedUsers ->
                (users + cachedUsers).distinctBy { user -> user.id }
            }
        }
    }

    override suspend fun observeUsers() {
        getUserSnapshotFlow().map { snapshot ->
            snapshot.documentChanges.partition { change ->
                change.type == ChangeType.REMOVED
            }.map { it.document.data(User.serializer()) }
        }.collect { (removedUsers, addedOrModifiedUsers) ->
            _users.update { users ->
                (addedOrModifiedUsers + users).distinctBy { user ->
                    user.id
                }.subtract(removedUsers.toSet()).toList()
            }
        }
    }

    private fun getUserSnapshotFlow() = combine(
        questionRepository.questions,
        barkochbaQuestionRepository.questions,
    ) { questions, barkochbaQuestions ->
        (questions.map { question ->
            listOfNotNull(question.userId, question.playerAnswer?.userId)
        }.flatten() + barkochbaQuestions.map { it.userId }).distinct()
    }.flatMapLatest { userIds ->
        if (userIds.isEmpty()) {
            flowOf()
        } else {
            firestore.collection(PLAYERS_COLLECTION_KEY).where {
                ID_FIELD_KEY inArray userIds
            }.snapshots
        }
    }

    private fun updateCachedUser(user: User) {
        _users.update { cachedUsers ->
            (listOf(user) + cachedUsers).distinctBy { it.id }
        }
    }

    companion object {
        private const val PLAYERS_COLLECTION_KEY = "players"
        private const val ID_FIELD_KEY = "id"
    }
}
