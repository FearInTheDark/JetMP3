package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vincent.jetmp3.ui.viewmodels.MusicViewModel
import okhttp3.internal.concurrent.formatDuration

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
	viewModel: MusicViewModel = hiltViewModel(),
) {
	val audioFiles by viewModel.audioFiles.collectAsState()

	Column(
		Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
			.padding(4.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {

		TopAppBar(
			title = { Text("JetMP3")}
		)

		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp)),
			contentPadding = PaddingValues(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			items(audioFiles) { audioFile ->
				Card (
					modifier = Modifier
						.fillMaxWidth()
						.clickable { viewModel.playSong(audioFile) }
						.animateItemPlacement(),
					shape = RoundedCornerShape(12.dp),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
					elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(12.dp),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Row(
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(12.dp)
						) {
							Box(
								modifier = Modifier
									.size(48.dp)
									.background(
										MaterialTheme.colorScheme.secondaryContainer,
										RoundedCornerShape(8.dp)
									)
							) {
								// Placeholder for album art or song icon
								Icon(
									imageVector = Icons.Default.MusicNote,
									contentDescription = null,
									modifier = Modifier.align(Alignment.Center),
									tint = MaterialTheme.colorScheme.onSecondaryContainer
								)
							}

							Column(
								verticalArrangement = Arrangement.spacedBy(2.dp),
								modifier = Modifier.widthIn(max = 220.dp)
							) {
								Text(
									text = audioFile.title,
									style = MaterialTheme.typography.bodyLarge,
									color = MaterialTheme.colorScheme.onSurface,
									maxLines = 1,
									overflow = TextOverflow.Ellipsis
								)
								Text(
									text = audioFile.artist,
									style = MaterialTheme.typography.bodySmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant,
									maxLines = 1,
									overflow = TextOverflow.Ellipsis
								)
							}
						}

						// Optional: Duration or play icon
						Text(
							text = formatDuration(audioFile.duration),
							style = MaterialTheme.typography.labelMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}
		}
	}

}
