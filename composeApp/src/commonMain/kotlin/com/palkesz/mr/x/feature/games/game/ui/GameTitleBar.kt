package com.palkesz.mr.x.feature.games.game.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.palkesz.mr.x.core.ui.components.titlebar.NavigationIcon
import com.palkesz.mr.x.core.util.extensions.capitalizeWords
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.game_screen_full_name_title
import mrx.composeapp.generated.resources.game_screen_hidden_name_title
import mrx.composeapp.generated.resources.ic_qr_code
import mrx.composeapp.generated.resources.ic_visibility_off
import mrx.composeapp.generated.resources.ic_visibility_on
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun GameTitleBar(
    firstName: String,
    lastName: String?,
    host: String,
    isIconVisible: Boolean,
    onQrCodeClicked: () -> Unit,
) {
    var isFullNameVisible by rememberSaveable { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = if (isFullNameVisible) {
                    stringResource(
                        Res.string.game_screen_full_name_title,
                        ("$firstName ${lastName ?: ""}").capitalizeWords(),
                        host,
                    )
                } else {
                    stringResource(
                        Res.string.game_screen_hidden_name_title,
                        (lastName?.firstOrNull() ?: firstName.first()).uppercase(),
                        host,
                    )
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        },
        navigationIcon = {
            NavigationIcon(navigationAction = { it?.popBackStack() })
        },
        actions = {
            NavigationIcon(
                iconVector = vectorResource(
                    if (isFullNameVisible) {
                        Res.drawable.ic_visibility_off
                    } else {
                        Res.drawable.ic_visibility_on
                    }
                ).takeIf { isIconVisible },
                navigationAction = { isFullNameVisible = !isFullNameVisible },
            )
            NavigationIcon(
                iconVector = vectorResource(Res.drawable.ic_qr_code),
                navigationAction = { onQrCodeClicked() },
            )
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
    )
}
