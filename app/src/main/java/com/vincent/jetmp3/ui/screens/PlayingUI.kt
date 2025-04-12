package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vincent.jetmp3.ui.viewmodels.MusicViewModel

@Composable
fun PlayingScreen(viewModel: MusicViewModel) {
	val currentSong by viewModel.currentSong.collectAsState()
	val isPlaying by viewModel.isPlaying.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		currentSong?.let { song ->
			Text(
				text = song.title, style = MaterialTheme.typography.headlineMedium,
				color = MaterialTheme.colorScheme.onSurface
			)
			Text(song.artist, style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurface)

			Spacer(modifier = Modifier.height(32.dp))
			Row {
				Button(onClick = { viewModel.playPrevious() }) {
					Text("Previous")
				}
				Spacer(modifier = Modifier.width(8.dp))
				Button(onClick = { viewModel.togglePlayPause() }) {
					Text(if (isPlaying) "Pause" else "Play")
				}
				Spacer(modifier = Modifier.width(8.dp))
				Button(onClick = { viewModel.playNext() }) {
					Text("Next")
				}
			}
		} ?: Text("No song playing", color = MaterialTheme.colorScheme.onSurface)
	}
}