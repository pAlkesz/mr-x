package com.palkesz.mr.x.core.data.user

import com.palkesz.mr.x.core.model.User
import com.palkesz.mr.x.core.util.networking.Error
import com.palkesz.mr.x.core.util.networking.Loading
import com.palkesz.mr.x.core.util.networking.Result
import com.palkesz.mr.x.core.util.networking.Success
import com.palkesz.mr.x.feature.network.NetworkErrorRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface UserRepository {

	fun uploadUser(user: User): Flow<Result<User>>

	fun getUser(uuid: String): Flow<Result<User>>

}

class UserRepositoryImpl(
	private val firestore: FirebaseFirestore,
	private val networkErrorRepository: NetworkErrorRepository
) : UserRepository {

	override fun uploadUser(user: User) = flow {
		try {
			firestore.collection(PLAYERS_KEY).document(user.userId).set(user)
			emit(Success(user))
		}
		catch (e: Exception) {
			networkErrorRepository.addError(e)
			emit(Error(e))
		}
	}

	override fun getUser(uuid: String) = flow {
		emit(Loading())
		try {
			val user =
				firestore.collection(PLAYERS_KEY).document(uuid).get().data(User.serializer())
			emit(Success(user))
		}
		catch (e: Exception) {
			emit(Error(e))
		}
	}

	companion object {
		const val PLAYERS_KEY = "players"
	}
}
