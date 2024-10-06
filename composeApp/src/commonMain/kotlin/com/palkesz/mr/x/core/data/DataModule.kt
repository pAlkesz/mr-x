package com.palkesz.mr.x.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl
import com.palkesz.mr.x.core.data.datastore.MrXDataStoreImpl
import com.palkesz.mr.x.core.data.datastore.MrxDataStore
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.game.GameRepositoryImpl
import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.data.game.QuestionRepositoryImpl
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.data.user.UserRepositoryImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::MrXDataStoreImpl) bind MrxDataStore::class
    includes(dataStoreModule)
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    single<GameRepository> { GameRepositoryImpl(get(), get(), get()) }
    single<QuestionRepository> { QuestionRepositoryImpl(get(), get(), get()) }
}

expect val dataStoreModule: Module

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

const val dataStoreFileName = "mrxapp.preferences_pb"
