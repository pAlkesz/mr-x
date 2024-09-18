package com.mr.x.core.util

import kotlinx.coroutines.flow.*

inline fun <T, R> Flow<Result<T>>.mapResult(crossinline block: suspend (T) -> R): Flow<Result<R>> =
	map { result ->
		result.map { block(it) }
	}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
	return when (this) {
		is Success -> Success(transform(result))
		is Error -> Error(result = result?.let { transform(it) }, error = error)
		is Loading -> Loading(result?.let { transform(it) }, error)
	}
}

inline fun <T, R> Flow<Result<T>>.flatMapResult(
	crossinline block: suspend (T) -> Flow<Result<R>>
): Flow<Result<R>> = flatMapLatest {
	when (it) {
		is Error -> flowOf(Error(it.error))
		is Loading -> flowOf(Loading())
		is Success -> block(it.result)
	}
}

fun <T> Flow<T>.prepend(value: T) = flowOf(flowOf(value), this).flattenMerge()

fun <T1, T2> Flow<Result<T1>>.combineResultPair(flow: Flow<Result<T2>>): Flow<Result<Pair<T1, T2>>> =
	combine(flow) { t1, t2 ->
		when {
			t1 is Error -> Error(t1.error)
			t2 is Error -> Error(t2.error)
			t1 is Success && t2 is Success -> Success(t1.result to t2.result)
			else -> Loading()
		}
	}
