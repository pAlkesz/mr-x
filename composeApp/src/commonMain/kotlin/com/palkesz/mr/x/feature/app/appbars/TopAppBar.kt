package com.palkesz.mr.x.feature.app.appbars

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.components.titlebar.CenteredTitleBar
import com.palkesz.mr.x.core.ui.components.titlebar.GameTitleBar
import com.palkesz.mr.x.core.ui.components.titlebar.NavigationIcon
import com.palkesz.mr.x.core.ui.providers.LocalTitleBarState
import com.palkesz.mr.x.feature.home.HomeGraph

@Composable
fun MrXTopAppBar(modifier: Modifier = Modifier) {
    val titleBarDetails = LocalTitleBarState.current.value
    CrossFade(
        titleBarDetails = titleBarDetails,
        centeredTitleBar = { details ->
            CenteredTitleBar(
                modifier = modifier,
                title = details.title,
                navigationIcon = {
                    AnimatedNullability(item = details.navigationIcon) { imageVector ->
                        NavigationIcon(imageVector = imageVector)
                    }
                },
                actions = {
                    AnimatedNullability(item = details.actionIcon) { imageVector ->
                        NavigationIcon(
                            imageVector = imageVector,
                            navigationAction = { navController ->
                                navController?.navigate(route = HomeGraph.Settings)
                            },
                        )
                    }
                }
            )
        },
        gameTitleBar = { details ->
            GameTitleBar(
                modifier = modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(insets = TopAppBarDefaults.windowInsets)
                    .padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 16.dp),
                firstName = details.firstName,
                lastName = details.lastName,
                hostName = details.hostName,
                isHost = details.isHost,
                onQrCodeClicked = details.onQrCodeClicked,
            )
        },
    )
}
