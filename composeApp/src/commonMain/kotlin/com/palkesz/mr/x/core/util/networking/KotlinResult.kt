package com.palkesz.mr.x.core.util.networking

import kotlin.Result

fun <T, R, G> combineResult(
    first: Result<T>,
    second: Result<R>,
    transform: (T, R) -> G,
): Result<G> {
    first.onSuccess { firstValue ->
        second.onSuccess { secondValue ->
            return Result.success(transform(firstValue, secondValue))
        }.onFailure { exception ->
            return Result.failure(exception = exception)
        }
    }.onFailure { exception ->
        return Result.failure(exception = exception)
    }
    return Result.failure(exception = Throwable())
}
