package com.palkesz.mr.x.core.data

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.game.GameRepositoryImpl
import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.data.game.QuestionRepositoryImpl
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.data.user.AuthRepositoryImpl
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.data.user.UserRepositoryImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
	single<FirebaseFirestore> { Firebase.firestore }
	single<FirebaseAuth> { Firebase.auth }

	single<UserRepository> { UserRepositoryImpl(get(), get()) }
	single<GameRepository> { GameRepositoryImpl(get(), get(), get()) }
	single<QuestionRepository> { QuestionRepositoryImpl(get(), get(), get()) }
	singleOf(::AuthRepositoryImpl) bind AuthRepository::class
}
