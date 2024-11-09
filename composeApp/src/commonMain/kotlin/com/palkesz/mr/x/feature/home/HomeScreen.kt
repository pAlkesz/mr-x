package com.palkesz.mr.x.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkesz.mr.x.core.ui.components.button.PrimaryCardButton
import com.palkesz.mr.x.core.ui.components.button.SecondaryCardButton
import com.palkesz.mr.x.core.ui.components.titlebar.CenteredTitleBar
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.create_game_button_label
import mrx.composeapp.generated.resources.create_game_description
import mrx.composeapp.generated.resources.create_game_title
import mrx.composeapp.generated.resources.home_screen_title
import mrx.composeapp.generated.resources.join_game_button_label
import mrx.composeapp.generated.resources.join_game_description
import mrx.composeapp.generated.resources.join_game_title
import mrx.composeapp.generated.resources.tutorial_button_label
import mrx.composeapp.generated.resources.tutorial_description
import mrx.composeapp.generated.resources.tutorial_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen() {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenteredTitleBar(
            title = stringResource(Res.string.home_screen_title),
            navigationIcon = null,
        )
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            HomeScreenCard(
                modifier = Modifier.padding(bottom = 16.dp),
                title = stringResource(Res.string.create_game_title),
                text = stringResource(Res.string.create_game_description),
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                PrimaryCardButton(
                    text = stringResource(Res.string.create_game_button_label),
                    onClick = { navController?.navigate(HomeGraph.CreateGame) },
                )
            }
            HomeScreenCard(
                modifier = Modifier.padding(bottom = 16.dp),
                title = stringResource(Res.string.join_game_title),
                text = stringResource(Res.string.join_game_description),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                PrimaryCardButton(
                    text = stringResource(Res.string.join_game_button_label),
                    onClick = { navController?.navigate(HomeGraph.JoinGame) },
                )
            }
            HomeScreenCard(
                title = stringResource(Res.string.tutorial_title),
                text = stringResource(Res.string.tutorial_description),
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                SecondaryCardButton(
                    text = stringResource(Res.string.tutorial_button_label),
                    onClick = { navController?.navigate(HomeGraph.Tutorial) },
                )
            }
        }
    }
}

@Composable
private fun HomeScreenCard(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    backgroundColor: Color,
    button: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            )
            button()
        }
    }
}
