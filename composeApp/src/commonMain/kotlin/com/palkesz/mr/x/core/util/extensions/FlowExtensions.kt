package com.palkesz.mr.x.core.util.extensions

import com.palkesz.mr.x.core.util.networking.Error
import com.palkesz.mr.x.core.util.networking.Loading
import com.palkesz.mr.x.core.util.networking.Result
import com.palkesz.mr.x.core.util.networking.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

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

fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combine(
    combine(flow, flow2, ::Pair),
    combine(flow3, flow4, ::Pair),
    combine(flow5, flow6, ::Pair)
) { t1, t2, t3 ->
    transform(
        t1.first,
        t1.second,
        t2.first,
        t2.second,
        t3.first,
        t3.second
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> = combine(
    combine(flow, flow2, ::Pair),
    combine(flow3, flow4, ::Pair),
    combine(flow5, flow6, flow7, ::Triple)
) { t1, t2, t3 ->
    transform(
        t1.first,
        t1.second,
        t2.first,
        t2.second,
        t3.first,
        t3.second,
        t3.third
    )
}
