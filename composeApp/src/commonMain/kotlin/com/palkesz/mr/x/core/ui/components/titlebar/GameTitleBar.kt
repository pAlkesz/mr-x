package com.palkesz.mr.x.core.ui.components.titlebar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.helpers.bold
import com.palkesz.mr.x.core.util.extensions.capitalizeWords
import com.palkesz.mr.x.feature.games.GameGraph
import com.palkesz.mr.x.feature.games.ui.GameIcon
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.game_host_label
import mrx.composeapp.generated.resources.game_screen_title_placeholder
import mrx.composeapp.generated.resources.game_title_with_initial
import mrx.composeapp.generated.resources.ic_host
import mrx.composeapp.generated.resources.ic_player
import mrx.composeapp.generated.resources.ic_qr_code
import mrx.composeapp.generated.resources.ic_visibility_off
import mrx.composeapp.generated.resources.ic_visibility_on
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun GameTitleBar(
    modifier: Modifier = Modifier,
    firstName: String?,
    lastName: String?,
    hostName: String?,
    isHost: Boolean,
    onQrCodeClicked: () -> Unit,
) {
    var isFullNameVisible by rememberSaveable { mutableStateOf(value = false) }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        NavigationIcon(navigationAction = { navController ->
            navController?.navigate(route = GameGraph.Games(joinedGameId = null))
        })
        GameIcon(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp),
            icon = vectorResource(if (isHost) Res.drawable.ic_host else Res.drawable.ic_player),
        )
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                modifier = Modifier.animateContentSize(),
                text = when {
                    firstName == null -> {
                        stringResource(Res.string.game_screen_title_placeholder)
                    }

                    isFullNameVisible -> {
                        ("$firstName ${lastName ?: ""}").capitalizeWords()
                    }

                    else -> {
                        stringResource(
                            Res.string.game_title_with_initial,
                            (lastName?.firstOrNull() ?: firstName.first()).uppercase(),
                        )
                    }
                },
                style = MaterialTheme.typography.titleMedium.bold()
            )
            AnimatedNullability(item = hostName) { name ->
                Text(
                    text = stringResource(Res.string.game_host_label, name),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
        NavigationIcon(
            iconVector = vectorResource(
                if (isFullNameVisible) {
                    Res.drawable.ic_visibility_off
                } else {
                    Res.drawable.ic_visibility_on
                }
            ).takeIf { isHost },
            navigationAction = { isFullNameVisible = !isFullNameVisible },
        )
        NavigationIcon(
            iconVector = vectorResource(Res.drawable.ic_qr_code),
            navigationAction = { onQrCodeClicked() },
        )
    }
}
