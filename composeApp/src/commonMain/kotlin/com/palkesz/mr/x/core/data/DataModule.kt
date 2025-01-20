package com.palkesz.mr.x.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl
import com.palkesz.mr.x.core.data.auth.FirebaseAuthentication
import com.palkesz.mr.x.core.data.auth.FirebaseAuthenticationImpl
import com.palkesz.mr.x.core.data.crashlytics.Crashlytics
import com.palkesz.mr.x.core.data.crashlytics.CrashlyticsImpl
import com.palkesz.mr.x.core.data.datastore.MrXDataSerializer
import com.palkesz.mr.x.core.data.datastore.MrXDataStoreImpl
import com.palkesz.mr.x.core.data.datastore.MrxDataStore
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.game.GameRepositoryImpl
import com.palkesz.mr.x.core.data.messaging.MessagingRepository
import com.palkesz.mr.x.core.data.messaging.MessagingRepositoryImpl
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepositoryImpl
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepositoryImpl
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.data.user.UserRepositoryImpl
import com.palkesz.mr.x.proto.MrXData
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.firestore.firestore
import okio.FileSystem
import okio.Path
import okio.SYSTEM
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.crashlytics }
    includes(dataStoreModule)
    singleOf(::FirebaseAuthenticationImpl) bind FirebaseAuthentication::class
    singleOf(::CrashlyticsImpl) bind Crashlytics::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::MrXDataStoreImpl) bind MrxDataStore::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::GameRepositoryImpl) bind GameRepository::class
    singleOf(::QuestionRepositoryImpl) bind QuestionRepository::class
    singleOf(::BarkochbaQuestionRepositoryImpl) bind BarkochbaQuestionRepository::class
    singleOf(::MessagingRepositoryImpl) bind MessagingRepository::class
}

expect val dataStoreModule: Module

fun createDataStore(producePath: () -> Path): DataStore<MrXData> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            producePath = producePath,
            serializer = MrXDataSerializer,
        ),
    )

const val DATA_STORE_FILE_NAME = "mrxapp.datastore_pb"
