package com.palkesz.mr.x.core.util.extensions

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
    safeLet(
        p1 = this?.takeIf { it.isNotBlank() }?.lowercase()?.toCharArray(),
        p2 = other?.takeIf { it.isNotBlank() }?.lowercase()?.toCharArray(),
    ) { first, second ->
        return when {
            first.size == second.size -> {
                first.isLessThanTwoReplaceAway(other = second)
            }

            first.size - 2 == second.size || first.size - 1 == second.size -> {
                first.isLessThanTwoInsertAway(other = second)
            }

            second.size - 2 == first.size || second.size - 1 == first.size -> {
                second.isLessThanTwoInsertAway(other = first)
            }

            else -> false
        }
    } ?: return true
}

fun CharArray.isLessThanTwoReplaceAway(other: CharArray): Boolean {
    var diffCount = 0
    forEachIndexed { index, char ->
        if (char != other[index]) {
            diffCount += 1
        }
    }
    return diffCount <= 2
}

fun CharArray.isLessThanTwoInsertAway(other: CharArray): Boolean {
    var diffCount = 0
    other.forEachIndexed { index, char ->
        if (this.getOrNull(index = index + diffCount) != char) {
            diffCount += 1
        }
    }
    return diffCount <= 2
}
