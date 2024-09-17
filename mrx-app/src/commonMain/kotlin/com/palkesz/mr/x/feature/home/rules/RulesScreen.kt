package com.palkesz.mr.x.feature.home.rules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.AnimatedNullability
import com.palkesz.mr.x.core.ui.theme.AppTypography
import com.palkesz.mr.x.core.ui.theme.onSecondaryContainerLight
import com.palkesz.mr.x.core.ui.theme.secondaryContainerLight
import com.palkesz.mr.x.feature.app.LocalAppState
import mrx.mrx_app.generated.resources.*
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun RulesScreen() {
	val appState = LocalAppState.current
	LaunchedEffect(Unit) {
		appState.apply {
			showTopAppBar()
			hideBottomAppBar()
			setScreenTitle(getString(Res.string.rules_title))
		}
	}

	Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
		RuleDescription(
			modifier = Modifier.padding(top = 12.dp),
			description = stringResource(Res.string.rules_opening)
		)
		RuleDescription(
			title = stringResource(Res.string.rules_1),
			description = stringResource(Res.string.rules_1_descr)
		)
		RuleDescription(
			title = stringResource(Res.string.rules_2),
			description = stringResource(Res.string.rules_2_descr)
		)
		RuleDescription(
			title = stringResource(Res.string.rules_3),
			description = stringResource(Res.string.rules_3_descr)
		)
		RuleDescription(
			title = stringResource(Res.string.rules_4),
			description = stringResource(Res.string.rules_4_descr)
		)
		RuleDescription(
			title = stringResource(Res.string.rules_5),
			description = stringResource(Res.string.rules_5_descr)
		)
	}
}

@Composable
fun RuleDescription(
	modifier: Modifier = Modifier,
	title: String? = null,
	description: String
) {
	ElevatedCard(
		elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
		modifier = modifier.padding(vertical = 8.dp, horizontal = 24.dp),
		colors = CardDefaults.cardColors(
			containerColor = secondaryContainerLight,
			contentColor = onSecondaryContainerLight)
	) {
		Column(modifier = Modifier.padding(8.dp)) {
			AnimatedNullability(title) {
				Text(
					text = it,
					modifier = Modifier.fillMaxWidth(),
					textAlign = TextAlign.Center,
					style = AppTypography.displayLarge
				)
			}
			Text(
				text = description,
				modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth(),
				style = AppTypography.bodyLarge,
				textAlign = TextAlign.Start
			)
		}
	}
}
