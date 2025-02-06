package com.palkesz.mr.x.feature.authentication.login

import androidx.compose.runtime.Immutable

@Immutable
data class LoginViewState(
    val email: String,
    val isEmailValid: Boolean,
    val isLinkSent: Boolean,
    val isSendButtonEnabled: Boolean,
)
