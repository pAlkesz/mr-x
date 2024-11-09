package com.palkesz.mr.x.core.util.extensions

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

inline fun <T, R : Any> Iterable<T>.immutableMapNotNull(transform: (T) -> R?): ImmutableList<R> =
    mapNotNull(transform).toImmutableList()

inline fun <T, R : Any> Iterable<T>.immutableMap(transform: (T) -> R): ImmutableList<R> =
    map(transform).toImmutableList()
