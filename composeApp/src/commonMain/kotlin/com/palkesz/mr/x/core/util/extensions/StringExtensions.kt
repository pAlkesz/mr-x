package com.palkesz.mr.x.core.util.extensions

fun String.trimmedEqualsIgnoreCase(other: String) =
    this.trim().equals(other.trim(), ignoreCase = true)

fun String.validateAsName() = all { it.isLetter() || it == ' ' }

fun String.validateAsUsername() = trim().all { it != ' ' } && length > 5 && length < 50

fun String.validateAsEmail() =
    matches(Regex("[a-zA-Z0-9+._%\\-]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"))

fun <T : CharSequence> T?.isNotNullOrBlank() = !this.isNullOrBlank()
