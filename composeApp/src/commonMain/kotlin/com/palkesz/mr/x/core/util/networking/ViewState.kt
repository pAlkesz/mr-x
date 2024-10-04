package com.palkesz.mr.x.core.util.networking

import com.palkesz.mr.x.core.util.extensions.asInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.Result

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

fun <T> ViewState<T>.getOrNull(): T? = asInstance<ViewState.Success<T>>()?.data

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

fun <T, R> ViewState<T>.map(transform: (T) -> R): ViewState<R> = when (this) {
    is ViewState.Loading -> this
    is ViewState.Success -> ViewState.Success(data = transform(this.data))
    is ViewState.Failure -> this
}

fun <T> MutableStateFlow<ViewState<T>>.updateIfSuccess(block: (T) -> T) =
    updateIfInstance<ViewState.Success<T>> {
        ViewState.Success(data = block(it.data))
    }

inline fun <reified T : Any> MutableStateFlow<in T>.updateIfInstance(function: (T) -> T): Boolean {
    while (true) {
        val prevValue = value.asInstance<T>() ?: return false
        val nextValue = function(prevValue)
        if (compareAndSet(prevValue, nextValue)) {
            return prevValue != nextValue
        }
    }
}
