package com.palkesz.mr.x.core.util

fun String.trimmedEqualsIgnoreCase(other: String) =
	this.trim().equals(other.trim(), ignoreCase = true)
