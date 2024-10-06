package com.palkesz.mr.x.core.data.user

import com.palkesz.mr.x.core.model.User
import dev.gitlive.firebase.firestore.FirebaseFirestore

interface UserRepository {
    suspend fun uploadUser(user: User): Result<User>
    suspend fun fetchUser(id: String): Result<User>
}

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : UserRepository {

    override suspend fun uploadUser(user: User) = try {
        firestore.collection(PLAYERS_COLLECTION_KEY).document(user.id).set(user)
        Result.success(user)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun fetchUser(id: String) = try {
        Result.success(
            firestore.collection(PLAYERS_COLLECTION_KEY).document(id).get().data(User.serializer())
        )
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    companion object {
        private const val PLAYERS_COLLECTION_KEY = "players"
    }
}
