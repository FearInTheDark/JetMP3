package com.vincent.jetmp3.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vincent.jetmp3.ui.theme.HeadLineMedium
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineSmall
import com.vincent.jetmp3.ui.viewmodels.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnotherPlayingScreen(
	viewModel: MusicViewModel = hiltViewModel(),
	onTopAppClick: () -> Unit = {}
) {

	val currentSong by viewModel.currentSong.collectAsState()
	val isPlaying by viewModel.isPlaying.collectAsState()
	val progress by viewModel.progress.collectAsState()

	val animProgress = remember { Animatable(0f) }

	LaunchedEffect(progress) {
		animProgress.animateTo(progress.toFloat(), animationSpec = tween(500))
	}

	val progressFloat = remember(currentSong, progress) {
		val duration = currentSong?.duration?.takeIf { it > 0 } ?: return@remember 0f
		(progress.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
	}

	val infiniteTransition = rememberInfiniteTransition(label = "albumPulse")
	val albumScale by infiniteTransition.animateFloat(
		initialValue = 1f,
		targetValue = 0.98f,
		animationSpec = infiniteRepeatable(
			animation = tween(1500, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse
		),
		label = "albumScale"
	)

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		currentSong?.let {
			TopAppBar(
				title = {
					Text(
						text = "Playing View",
						style = HeadStyleLarge,
						color = MaterialTheme.colorScheme.onSurface
					)
				},
				navigationIcon = {
					IconButton(
						onClick = { onTopAppClick() }
					) {
						Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
					}
				},
				actions = {
					IconButton(
						onClick = {}
					) {
						Icon(
							Icons.Outlined.MoreVert,
							null,
						)
					}
				},
			)

			Box(
				Modifier
					.fillMaxSize()
					.padding(4.dp),
				contentAlignment = Alignment.Center
			) {

				Column(
					Modifier
						.fillMaxSize()
						.padding(horizontal = 8.dp),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					AsyncImage(
						model = "https://i.scdn.co/image/ab67616d0000b273b5097b81179824803664aaaf",
						contentDescription = "Image",
						contentScale = ContentScale.Crop,
						modifier = Modifier
							.scale(if (isPlaying) albumScale else 1f)
							.clip(RoundedCornerShape(10.dp))
							.fillMaxWidth(0.95f)
							.aspectRatio(1f)

					)

					Spacer(Modifier.height(24.dp))

					Row(
						Modifier
							.fillMaxWidth()
							.padding(6.dp),
						Arrangement.SpaceBetween,
						Alignment.CenterVertically
					) {
						Column(
							modifier = Modifier
								.wrapContentSize()
								.padding(2.dp),
							verticalArrangement = Arrangement.Center,
							horizontalAlignment = Alignment.Start
						) {
							Text(
								text = currentSong?.title ?: "Unknown",
								style = HeadLineMedium,
								color = MaterialTheme.colorScheme.onSurface
							)

							Text(
								text = currentSong?.artist ?: "Taylor Swift",
								style = MaterialTheme.typography.labelMedium,
								color = MaterialTheme.colorScheme.onSurface
							)
						}
					}

					Spacer(Modifier.height(12.dp))

					Box(
						Modifier
							.fillMaxWidth()
							.wrapContentSize(),
						contentAlignment = Alignment.Center
					) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							LinearProgressIndicator(
								progress = { progressFloat },
								color = Color.White,
								modifier = Modifier
									.fillMaxWidth(0.95f)
									.height(4.dp),
							)
							Spacer(Modifier.height(4.dp))

							Row(
								Modifier
									.fillMaxWidth(0.95f)
									.padding(
										vertical = 4.dp,
										horizontal = 4.dp
									),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.SpaceBetween,
							) {
								Text(
//									text = durationToString(progress),
									text = "1:00",
									color = MaterialTheme.colorScheme.onSurface,
									style = LabelLineSmall
								)
								Text(
//									text = durationToString(currentSong?.duration),
									text = progress.toString(),
									color = MaterialTheme.colorScheme.onSurface,
									style = LabelLineSmall
								)
							}
						}

					}
				}
			}
		}
	}
}

fun durationToString(duration: Long?): String {
	if (duration == null || duration <= 0) {
		return "00:00"
	}

	val minutes = (duration / 1000) / 60
	val seconds = (duration / 1000) % 60

	return String.format(null, "%02d:%02d", minutes, seconds)
}