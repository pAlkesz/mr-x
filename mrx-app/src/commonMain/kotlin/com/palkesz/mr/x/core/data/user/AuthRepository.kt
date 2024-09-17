package com.palkesz.mr.x.core.data.user

import com.palkesz.mr.x.core.model.User
import com.palkesz.mr.x.core.util.*
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mrx.mrx_app.generated.resources.Res
import mrx.mrx_app.generated.resources.no_name_found_placeholder
import mrx.mrx_app.generated.resources.no_user_error_message
import org.jetbrains.compose.resources.getString

interface AuthRepository {
	val currentUserId: String?
	val isAuthenticated: Boolean
	val currentUser: Flow<User?>

	fun authenticate(email: String, password: String): Flow<Result<Unit>>
	fun createUser(email: String, password: String, username: String): Flow<Result<String>>
}

class AuthRepositoryImpl(
	private val auth: FirebaseAuth
) : AuthRepository {

	override var currentUserId = auth.currentUser?.uid
	override var isAuthenticated = auth.currentUser != null
	override var currentUser = auth.authStateChanged.map {
		it?.let {
			User(
				userId = it.uid,
				userName = it.displayName ?: getString(Res.string.no_name_found_placeholder)
			)
		}
	}

	init {
		CoroutineHelper.ioScope.launch {
			auth.authStateChanged.collect {
				isAuthenticated = it != null
				currentUserId = it?.uid
			}
		}
	}

	override fun authenticate(email: String, password: String) = flow {
		emit(Loading(Unit))
		try {
			auth.signInWithEmailAndPassword(email, password)
			emit(Success(Unit))
		}
		catch (e: Exception) {
			emit(Error(e))
		}
	}

	override fun createUser(
		email: String,
		password: String,
		username: String
	) = flow<Result<String>> {
		emit(Loading())
		try {
			val user = auth.createUserWithEmailAndPassword(email, password).user
			user?.updateProfile(displayName = username)
			user?.let {
				emit(Success(user.uid))
			} ?: emit(Error(Exception(getString(Res.string.no_user_error_message))))
		}
		catch (e: Exception) {
			emit(Error(e))
		}
	}
}
