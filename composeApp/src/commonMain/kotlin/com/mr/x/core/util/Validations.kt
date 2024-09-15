package com.mr.x.core.util

fun validateName(name: String): Boolean {
	return name.all { it.isLetter() || it == ' ' }
}

fun validateEmail(email: String): Boolean {
	val emailAddressRegex = Regex(
		"[a-zA-Z0-9+._%\\-]{1,256}" +
				"@" +
				"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
				"(" +
				"\\." +
				"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
				")+"
	)
	return email.matches(emailAddressRegex)
}
