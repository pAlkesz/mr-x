package com.palkesz.mr.x.core.usecase.auth

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.datastore.MrxDataStore

class SendSignInLinkUseCase(
    private val authRepository: AuthRepository,
    private val mrxDataStore: MrxDataStore,
) {

    suspend fun run(email: String) = mrxDataStore.storeUserEmail(email = email).fold(onSuccess = {
        authRepository.sendSignInLinkToEmail(email = email)
    }, onFailure = { exception ->
        Result.failure(exception = exception)
    })

}
