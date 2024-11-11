package com.palkesz.mr.x.core.util.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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
        t3.second,
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
        t3.third,
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Flow<R> = combine(
    combine(flow, flow2, flow3, ::Triple),
    combine(flow4, flow5, flow6, ::Triple),
    combine(flow7, flow8, flow9, ::Triple),
) { t1, t2, t3 ->
    transform(
        t1.first,
        t1.second,
        t1.third,
        t2.first,
        t2.second,
        t2.third,
        t3.first,
        t3.second,
        t3.third,
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
): Flow<R> = combine(
    combine(flow, flow2, ::Pair),
    combine(flow3, flow4, ::Pair),
    combine(flow5, flow6, flow7, ::Triple),
    combine(flow8, flow9, flow10, ::Triple)
) { t1, t2, t3, t4 ->
    transform(
        t1.first,
        t1.second,
        t2.first,
        t2.second,
        t3.first,
        t3.second,
        t3.third,
        t4.first,
        t4.second,
        t4.third,
    )
}
