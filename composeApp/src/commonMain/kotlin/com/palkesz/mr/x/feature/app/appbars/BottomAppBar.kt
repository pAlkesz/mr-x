package com.palkesz.mr.x.feature.app.appbars

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.palkesz.mr.x.core.ui.components.badge.ContentWithBadge
import com.palkesz.mr.x.core.ui.modifiers.debouncedClickable
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.feature.games.GameGraph
import com.palkesz.mr.x.feature.home.HomeGraph
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_game_controller
import mrx.composeapp.generated.resources.ic_home
import org.jetbrains.compose.resources.vectorResource

@Composable
fun MrXBottomAppBar(gameNotificationCount: Int?) {
    val navController = LocalNavController.current ?: throw IllegalStateException()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = bottomBarNavigationRoutes.any {
        currentDestination?.hasRoute(it::class) ?: false
    }

    if (bottomBarDestination) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(insets = NavigationBarDefaults.windowInsets)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavigationBarItem(
                currentDestination = currentDestination,
                route = HomeGraph.Home,
                icon = vectorResource(Res.drawable.ic_home),
            )
            NavigationBarItem(
                currentDestination = currentDestination,
                route = GameGraph.Games(joinedGameId = null),
                icon = vectorResource(Res.drawable.ic_game_controller),
                badgeCount = gameNotificationCount,
            )
        }
    }
}

@Composable
private fun NavigationBarItem(
    currentDestination: NavDestination?,
    route: Any,
    icon: ImageVector,
    badgeCount: Int? = null,
) {
    val navController = LocalNavController.current
    val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
    val backgroundColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        }
    )
    val iconColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.primary
        }
    )
    ContentWithBadge(badgeCount = badgeCount) {
        Box(
            modifier = Modifier
                .size(size = 48.dp)
                .clip(shape = CircleShape)
                .debouncedClickable(enabled = !isSelected) { navController?.navigate(route) }
                .background(color = backgroundColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(size = 32.dp),
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
            )
        }
    }
}

private val bottomBarNavigationRoutes = listOf(HomeGraph.Home, GameGraph.Games(joinedGameId = null))
