package com.vincent.jetmp3.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.viewmodels.AudioViewModel
import com.vincent.jetmp3.ui.viewmodels.UIEvent
import com.vincent.jetmp3.ui.viewmodels.UIState
import okhttp3.internal.concurrent.formatDuration

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
	viewModel: AudioViewModel = hiltViewModel(),
	onItemClick: () -> Unit
) {
	val audioFiles: List<AudioFile> = viewModel.audioList
	val refreshing = (viewModel.uiState.value == UIState.Fetching)
	LocalContext.current

	PullToRefreshBox(
		isRefreshing = refreshing,
		onRefresh = { viewModel.onUiEvent(UIEvent.FetchAudio) },
	) {
		Column(
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.surface)
				.padding(4.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {

			TopAppBar(
				title = {
					Text(
						text = "JetMP3",
						style = HeadStyleLarge
					)
				},
				actions = {
					Icon(
						imageVector = Icons.Default.MusicNote,
						contentDescription = "Now Playing",
						modifier = Modifier
							.padding(8.dp)
							.clickable { /* Navigate to Now Playing screen */ }
					)
				},
			)

			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp)),
				contentPadding = PaddingValues(
					top = 12.dp,
					bottom = 150.dp
				),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				itemsIndexed(audioFiles) { index, audioFile ->
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.clickable {
								viewModel.onUiEvent(UIEvent.SelectedAudioChange(index))
								onItemClick()
							},
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
										text = audioFile.displayName,
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

}
