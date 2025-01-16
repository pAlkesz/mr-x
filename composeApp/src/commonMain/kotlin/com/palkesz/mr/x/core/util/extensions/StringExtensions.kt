package com.palkesz.mr.x.core.util.extensions

import com.palkesz.mr.x.core.util.levenshtein
import doist.x.normalize.Form
import doist.x.normalize.normalize

fun String.validateAsName() = all { it.isLetter() || it == ' ' }

fun String.validateAsUsername() = trim().all { it != ' ' } && length > 5 && length < 20

fun String.validateAsEmail() =
    matches(Regex("[a-zA-Z0-9+._%\\-]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"))

fun <T : CharSequence> T?.isNotNullOrBlank() = !this.isNullOrBlank()

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

fun String.capitalizeFirstChar(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun String.normalize() =
    normalize(Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")

fun String?.equalsAsName(other: String?) =
    this?.trim()?.normalize().isSimilar(other?.trim()?.normalize())

fun String?.isSimilar(other: String?): Boolean {
    if (equals(other = other, ignoreCase = true)) {
        return true
    }
    return levenshtein(lhs = this.orEmpty().lowercase(), rhs = other.orEmpty().lowercase()) <= 2
}
