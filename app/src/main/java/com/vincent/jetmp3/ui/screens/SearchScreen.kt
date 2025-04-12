package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
	Text(
		text = "Search",
		modifier = modifier
			.fillMaxSize()
			.padding(16.dp),
		color = MaterialTheme.colorScheme.onSurface
	)
}