package com.palkesz.mr.x.core.util.networking

import kotlinx.coroutines.flow.MutableStateFlow

interface ViewState<out T> where T : Any? {
	data object Loading : ViewState<Nothing>

	data class Success<T>(val data: T) : ViewState<T>

	data object Failure : ViewState<Nothing>
}

fun <T> ViewState<T>.getOrNull(): T? = asInstance<ViewState.Success<T>>()?.data

inline fun <reified R : Any> Any?.asInstance() = this as? R

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
