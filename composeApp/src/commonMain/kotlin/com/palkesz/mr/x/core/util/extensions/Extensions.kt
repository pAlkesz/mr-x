package com.palkesz.mr.x.core.util.extensions

import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.LINK_PARAMETER_KEY
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.question.Answer
import com.palkesz.mr.x.core.util.platform.isAndroid
import dev.theolm.rinku.DeepLink

inline fun <reified R : Any> Any?.asInstance() = this as? R

fun DeepLink.getSignInLink() = if (isAndroid) toString() else parameters[LINK_PARAMETER_KEY]

inline fun <T, R> Pair<List<T>, List<T>>.map(transform: (T) -> R) =
    first.map(transform) to second.map(transform)

fun Game.getInitial() = lastName?.firstOrNull() ?: firstName.firstOrNull()

fun Answer.getName() = "$firstName ${lastName.orEmpty()}".trim()

fun Answer.isGuessed(firstName: String, lastName: String?) =
    this.firstName.equalsAsName(firstName) && this.lastName.equalsAsName(lastName)

fun Pair<String, String>.validateAsName(gameInitial: Char?): Pair<Boolean, Boolean> {
    val isFirstNameValid = first.isNotBlank() && first.validateAsName() &&
            (second.isNotBlank() || gameInitial?.let {
                first.firstOrNull()?.equals(other = it, ignoreCase = true)
            } ?: false)
    val isLastNameValid =
        second.validateAsName() && (second.isBlank() || second.firstOrNull() == gameInitial)
    return isFirstNameValid to isLastNameValid
}
