package com.palkesz.mr.x.core.util.networking

sealed class Result<T> {
    abstract val result: T?
    abstract val error: Throwable?

    val value: T?
        get() = result

    val isLoading: Boolean
        get() = this is Loading

    val isError
        get() = this is Error

    val isSuccess: Boolean
        get() = this is Success
}

data class Success<T>(
    override val result: T,
    override val error: Throwable? = null
) : Result<T>()

data class Loading<T>(
    override val result: T? = null,
    override val error: Throwable? = null
) : Result<T>()

data class Error<T>(
    override val error: Throwable,
    override val result: T? = null
) : Result<T>()

inline fun <T> Result<T>.onSuccess(block: (T) -> Unit) = this.also {
    if (this is Success) {
        block(this.result)
    }
}
