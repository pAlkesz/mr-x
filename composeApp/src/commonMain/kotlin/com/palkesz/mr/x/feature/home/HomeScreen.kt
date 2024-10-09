package com.palkesz.mr.x.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.palkesz.mr.x.core.ui.components.BaseCard
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.create_game
import mrx.composeapp.generated.resources.create_game_descr
import mrx.composeapp.generated.resources.join_game
import mrx.composeapp.generated.resources.join_game_descr
import mrx.composeapp.generated.resources.rules_button_label
import mrx.composeapp.generated.resources.rules_card_description
import mrx.composeapp.generated.resources.rules_title

@Composable
fun HomeScreen() {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BaseCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController?.navigate(HomeGraphRoute.CreateGame.route) },
            title = Res.string.create_game,
            description = Res.string.create_game_descr,
            buttonLabel = Res.string.create_game,
        )
        BaseCard(
            onClick = { navController?.navigate(HomeGraphRoute.JoinGame.route) },
            title = Res.string.join_game,
            description = Res.string.join_game_descr,
            buttonLabel = Res.string.join_game,
        )
        BaseCard(
            onClick = { navController?.navigate(HomeGraphRoute.Rules.route) },
            title = Res.string.rules_title,
            description = Res.string.rules_card_description,
            buttonLabel = Res.string.rules_button_label,
        )
    }
}
