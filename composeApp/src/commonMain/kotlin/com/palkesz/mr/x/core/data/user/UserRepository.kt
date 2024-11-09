package com.palkesz.mr.x.core.data.user

import com.palkesz.mr.x.core.model.user.User
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface UserRepository {
    val users: StateFlow<List<User>>

    suspend fun uploadUser(user: User): Result<User>
    suspend fun fetchUser(id: String): Result<User>
    suspend fun fetchUsers(ids: List<String>): Result<List<User>>
}

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : UserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users = _users.asStateFlow()

    override suspend fun uploadUser(user: User) = try {
        firestore.collection(PLAYERS_COLLECTION_KEY).document(user.id).set(user)
        updateCachedUser(user = user)
        Result.success(user)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun fetchUser(id: String) = try {
        val user =
            firestore.collection(PLAYERS_COLLECTION_KEY).document(id).get().data(User.serializer())
        updateCachedUser(user = user)
        Result.success(user)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun fetchUsers(ids: List<String>): Result<List<User>> {
        if (users.value.map { it.id }.containsAll(elements = ids)) {
            return Result.success(users.value.filter { it.id in ids })
        }
        return try {
            val users = firestore.collection(PLAYERS_COLLECTION_KEY).where {
                ID_FIELD_KEY inArray ids
            }.get().documents.map { it.data(User.serializer()) }
            _users.update { cachedUsers ->
                (users + cachedUsers).distinctBy { user -> user.id }
            }
            Result.success(users)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
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
