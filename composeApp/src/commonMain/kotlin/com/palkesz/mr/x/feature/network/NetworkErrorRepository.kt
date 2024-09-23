package com.palkesz.mr.x.feature.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NetworkErrorRepository {
    private val _error = MutableSharedFlow<Throwable>()
    val error = _error.asSharedFlow()

    suspend fun addError(error: Throwable) {
        _error.emit(error)
    }
}
