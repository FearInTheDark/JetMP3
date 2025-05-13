package com.vincent.jetmp3.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun LoadingOverlay(
	isLoading: Boolean,
	modifier: Modifier = Modifier,
	backgroundColor: Color = Color.Black.copy(if (isSystemInDarkTheme()) 0.5f else 0.3f),
	icon: String = "loading.json",
	iconModifier: Modifier = Modifier.size(100.dp)
) {
	if (isLoading) {
		Box(
			modifier = modifier
				.fillMaxSize()
				.background(backgroundColor)
				.zIndex(1f),
			contentAlignment = Alignment.Center
		) {
			LoadingHolder(icon = icon, modifier = iconModifier)
		}
	}
}
