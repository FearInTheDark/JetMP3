package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vincent.jetmp3.ui.viewmodels.MusicViewModel

@Composable
fun MusicApp() {
	val viewModel: MusicViewModel = viewModel()

	Box(
		Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.surface),
		contentAlignment = Alignment.Center,
	) {
		SongListScreen(viewModel)

	}



}

@Composable
fun SongListScreen(viewModel: MusicViewModel) {
	val audioFiles by viewModel.audioFiles.collectAsState()
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp),
	) {
		items(audioFiles) { audioFile ->
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { viewModel.playSong(audioFile) }
					.padding(8.dp)
			) {
				Column {
					Text(
						text = audioFile.title,
						color = MaterialTheme.colorScheme.onSurface
					)
					Text(
						audioFile.artist, style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurface
					)
				}
			}
		}
	}
}