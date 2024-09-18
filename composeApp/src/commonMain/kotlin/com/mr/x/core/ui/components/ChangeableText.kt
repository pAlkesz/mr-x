package com.mr.x.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.visibility_off
import mrx.composeapp.generated.resources.visibility_on
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ChangeableText(
	modifier: Modifier = Modifier,
	defaultText: String,
	hiddenText: String?,
	style: TextStyle = LocalTextStyle.current
) {
	hiddenText?.let {
		var defaultTextShown by remember { mutableStateOf(true) }
		Row(
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically,
			modifier = modifier
				.clip(RoundedCornerShape(30))
				.clickable(onClick = {
					defaultTextShown = defaultTextShown.not()
				})
				.padding(horizontal = 4.dp)
		) {
			Text(
				text = if (defaultTextShown) defaultText else it,
				style = style
			)
			Icon(
				imageVector = if (defaultTextShown)
					vectorResource(Res.drawable.visibility_off)
				else
					vectorResource(Res.drawable.visibility_on),
				contentDescription = null,
				modifier = Modifier.padding(start = 8.dp)
			)
		}
	} ?: Text(
		text = defaultText,
		style = style,
		modifier = modifier.padding(horizontal = 4.dp)
	)
}
