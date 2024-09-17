package com.palkesz.mr.x.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val AppTypography = Typography(
	bodySmall = TextStyle(fontSize = 16.sp, lineHeight = 20.sp),
	headlineLarge = TextStyle(
		fontSize = 44.sp,
		lineHeight = 56.sp,
		fontWeight = FontWeight.Bold),
	displayLarge = TextStyle(
		fontSize = 20.sp,
		lineHeight = 22.sp,
		fontWeight = FontWeight.Bold
	),
	labelSmall = TextStyle(
		fontSize = 16.sp,
		fontStyle = FontStyle.Italic,
		color = Color.Gray
	)
)
