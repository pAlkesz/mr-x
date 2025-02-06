package com.palkesz.mr.x.feature.home.settings

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsViewState(val event: SettingsEvent? = null)

@Immutable
sealed interface SettingsEvent {

    data object ShowDeleteAccountDialog : SettingsEvent

}
