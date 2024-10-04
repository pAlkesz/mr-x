package com.palkesz.mr.x.feature.authentication.username

import androidx.compose.runtime.Immutable

@Immutable
data class AddUserNameViewState(
    val username: String,
    val isUserNameValid: Boolean,
    val event: AddUserNameEvent? = null
)

@Immutable
sealed interface AddUserNameEvent {

    data object NavigateToHome : AddUserNameEvent

}
