package com.palkesz.mr.x.core.util.extensions

import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.LINK_PARAMETER_KEY
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.question.Answer
import com.palkesz.mr.x.core.util.platform.isAndroid
import dev.theolm.rinku.DeepLink

inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

inline fun <reified R : Any> Any?.asInstance() = this as? R

fun DeepLink.getSignInLink() = if (isAndroid) toString() else parameters[LINK_PARAMETER_KEY]

inline fun <T, R> Pair<List<T>, List<T>>.map(transform: (T) -> R) =
    first.map(transform) to second.map(transform)

fun Game.getInitial() = lastName?.firstOrNull() ?: firstName.firstOrNull()

fun Answer.getName() = "$firstName $lastName"
