package com.palkesz.mr.x.core.util.networking

sealed interface ViewState<out T> {

    val isLoading: Boolean

    data object Loading : ViewState<Nothing> {
        override val isLoading = true
    }

    data class Success<T>(
        val data: T,
        override val isLoading: Boolean = false,
    ) : ViewState<T>

    data class Failure(override val isLoading: Boolean = false) : ViewState<Nothing>

}

fun <T> ViewState<T>.toLoading() = when (this) {
    is ViewState.Loading -> this
    is ViewState.Success -> this.copy(isLoading = true)
    is ViewState.Failure -> this.copy(isLoading = true)
}

fun <T> Result<T>.toViewState() = fold(
    onSuccess = { data ->
        ViewState.Success(data = data)
    }, onFailure = {
        ViewState.Failure()
    })

suspend fun <T, R> ViewState<T>.map(transform: suspend (T) -> R): ViewState<R> = when (this) {
    is ViewState.Loading -> this
    is ViewState.Success -> ViewState.Success(data = transform(this.data))
    is ViewState.Failure -> this
}
