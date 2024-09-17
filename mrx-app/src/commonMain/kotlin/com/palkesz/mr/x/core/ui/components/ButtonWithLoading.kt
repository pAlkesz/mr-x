package com.palkesz.mr.x.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.util.DebouncedButton
import mrx.mrx_app.generated.resources.Res
import mrx.mrx_app.generated.resources.sign_up_button_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun ButtonWithLoading(
	modifier: Modifier = Modifier,
	isLoading: Boolean,
	onClick: () -> Unit,
	shape: Shape = CustomRoundedCornerShape()
) {
	CrossFade(
		modifier = modifier.fillMaxWidth(),
		condition = isLoading,
		onConditionFalse = {
			DebouncedButton(
				onClick = onClick,
				shape = shape,
				modifier = Modifier.fillMaxWidth().padding(12.dp)
			) {
				Text(
					text = stringResource(Res.string.sign_up_button_label),
					style = MaterialTheme.typography.headlineMedium
				)
			}
		},
		onConditionTrue = {
			Box(modifier = Modifier.fillMaxWidth()) {
				CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
			}
		}
	)
}
