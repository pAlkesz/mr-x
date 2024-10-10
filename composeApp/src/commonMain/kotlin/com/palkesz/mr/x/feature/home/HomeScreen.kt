package com.palkesz.mr.x.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palkesz.mr.x.core.ui.components.text.CenteredTitleBar
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
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenteredTitleBar(
            title = stringResource(Res.string.home_screen_title),
            navigationIcon = null,
        )
        HomeScreenCard(
            modifier = Modifier.padding(bottom = 16.dp),
            title = stringResource(Res.string.create_game_title),
            text = stringResource(Res.string.create_game_description),
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
            HomePrimaryButton(
                text = stringResource(Res.string.create_game_button_label),
                onClick = { navController?.navigate(HomeGraphRoute.CreateGame.route) },
            )
        }
        HomeScreenCard(
            modifier = Modifier.padding(bottom = 16.dp),
            title = stringResource(Res.string.join_game_title),
            text = stringResource(Res.string.join_game_description),
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            HomePrimaryButton(
                text = stringResource(Res.string.join_game_button_label),
                onClick = { navController?.navigate(HomeGraphRoute.JoinGame.route) },
            )
        }
        HomeScreenCard(
            title = stringResource(Res.string.tutorial_title),
            text = stringResource(Res.string.tutorial_description),
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
            HomeSecondaryButton(
                text = stringResource(Res.string.tutorial_button_label),
                onClick = { navController?.navigate(HomeGraphRoute.Rules.route) },
            )
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
                modifier = Modifier.padding(bottom = 16.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 25.sp),
                modifier = Modifier.padding(bottom = 16.dp, end = 70.dp)
            )
            button()
        }
    }
}

@Composable
private fun HomePrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.widthIn(max = 488.dp).fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            )
        }
    )
}

@Composable
private fun HomeSecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.widthIn(max = 488.dp).fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors()
            .copy(contentColor = MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(10.dp),
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            )
        }
    )
}
