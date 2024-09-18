package com.mr.x.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mr.x.core.util.debouncedClickable
import com.mr.x.feature.app.LocalAppState
import com.mr.x.feature.app.LocalNavController

@Composable
fun MrXTopAppBar() {
	val appState = LocalAppState.current
	val navController = LocalNavController.current
	TopAppBar(
		title = {
			ChangeableText(
				defaultText = appState.currentAppData.screenTitle,
				hiddenText = appState.currentAppData.optionalScreenTitle,
				style = MaterialTheme.typography.headlineMedium
			)
		},
		navigationIcon = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = null,
				modifier = Modifier
					.minimumInteractiveComponentSize()
					.size(40.dp)
					.clip(CircleShape)
					.debouncedClickable {
						navController?.navigateUp()
					}
			)
		},
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer
		)
	)
}
