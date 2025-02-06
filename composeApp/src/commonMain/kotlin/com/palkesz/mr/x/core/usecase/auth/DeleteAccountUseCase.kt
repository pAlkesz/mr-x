package com.palkesz.mr.x.core.usecase.auth

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.functions.FirebaseFunctions

fun interface DeleteAccountUseCase {
    suspend fun run(): Result<Unit>
}

class DeleteAccountUseCaseImpl(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
) : DeleteAccountUseCase {

    override suspend fun run(): Result<Unit> = runCatching {
        functions.httpsCallable(name = "deleteAccount").invoke()
        auth.signOut()
    }
}
