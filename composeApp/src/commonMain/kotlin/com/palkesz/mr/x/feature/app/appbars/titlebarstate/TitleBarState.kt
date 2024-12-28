package com.palkesz.mr.x.feature.app.appbars.titlebarstate

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
sealed interface TitleBarDetails {

    data class CenteredTitleBarDetails(
        val title: String,
        val navigationIcon: ImageVector? = Icons.AutoMirrored.Filled.ArrowBack,
    ) : TitleBarDetails

    data class GameTitleBarDetails(
        val firstName: String?,
        val lastName: String?,
        val hostName: String?,
        val isHost: Boolean,
        val onQrCodeClicked: () -> Unit,
    ) : TitleBarDetails

}

@Stable
class TitleBarState {

    var value: TitleBarDetails? by mutableStateOf(value = null)
        private set

    fun update(details: TitleBarDetails?) {
        value = details
    }
}
