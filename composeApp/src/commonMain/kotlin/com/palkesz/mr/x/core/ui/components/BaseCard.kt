package com.palkesz.mr.x.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.ui.theme.*
import com.palkesz.mr.x.core.util.DebouncedButton
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.qr_code_scanner_24px
import mrx.composeapp.generated.resources.schedule
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BaseCard(
	title: StringResource,
	description: StringResource,
	buttonLabel: StringResource,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	ElevatedCard(
		elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
		modifier = modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp),
	) {
		Column {
			Text(
				text = stringResource(title),
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.padding(12.dp)
			)
			Text(
				text = stringResource(description),
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Start,
				modifier = Modifier.padding(horizontal = 12.dp)
			)
			DebouncedButton(
				onClick = onClick,
				shape = RoundedCornerShape(8.dp),
				modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
					.padding(8.dp),
			) {
				Text(
					text = stringResource(buttonLabel),
					style = MaterialTheme.typography.bodyLarge
				)
			}
		}
	}
}

@Composable
fun GameCard(
	defaultTitle: String,
	hiddenTitle: String?,
	buttonLabel: String,
	onClick: () -> Unit,
	onQrCodeClick: () -> Unit,
	isHost: Boolean,
	status: GameStatus,
	modifier: Modifier = Modifier
) {
	ElevatedCard(
		elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
		modifier = modifier.padding(horizontal = 24.dp, vertical = 10.dp),
		colors = if (isHost) {
			CardDefaults.cardColors().copy(
				containerColor = primaryContainerLight,
				contentColor = onPrimaryContainerLight)
		}
		else {
			CardDefaults.cardColors().copy(
				containerColor = tertiaryContainerLight,
				contentColor = onTertiaryContainerLight)
		}
	) {
		Column {
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()) {
				ChangeableText(
					defaultText = defaultTitle,
					hiddenText = hiddenTitle,
					style = MaterialTheme.typography.headlineMedium,
					modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
				)
				Spacer(modifier = Modifier.weight(1f))
				Icon(
					imageVector = when (status) {
						GameStatus.ONGOING -> vectorResource(Res.drawable.schedule)
						GameStatus.FINISHED -> Icons.Outlined.CheckCircle
					},
					modifier = Modifier.size(48.dp).padding(end = 16.dp, top = 8.dp),
					contentDescription = null
				)
				Icon(
					imageVector = vectorResource(Res.drawable.qr_code_scanner_24px),
					contentDescription = null,
					modifier = Modifier.size(48.dp).padding(end = 16.dp, top = 8.dp)
						.clickable(onClick = onQrCodeClick))
			}
			DebouncedButton(
				onClick = onClick,
				shape = RoundedCornerShape(8.dp),
				modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
					.padding(8.dp),
				colors = if (isHost) {
					ButtonDefaults.buttonColors()
				}
				else {
					ButtonDefaults.buttonColors().copy(
						containerColor = tertiaryLight,
						contentColor = onTertiaryLight)
				}
			) {
				Text(
					text = buttonLabel,
					style = MaterialTheme.typography.bodyLarge
				)
			}
		}
	}
}

