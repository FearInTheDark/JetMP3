package com.vincent.jetmp3.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.basicMarquee
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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Slider
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

	val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

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

	BottomSheetScaffold(
		scaffoldState = bottomSheetScaffoldState,
		sheetContent = @Composable {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Top
			) {
				Text(
					text = "Bottom Sheet Content",
					style = HeadLineMedium,
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		},
		topBar = {
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
		},
		sheetPeekHeight = 50.dp,
		containerColor = MaterialTheme.colorScheme.surface,
		content = @Composable {innerPadding ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Top
			) {
				currentSong?.let {
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
										color = MaterialTheme.colorScheme.onSurface,
										modifier = Modifier.basicMarquee()
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
//							LinearProgressIndicator(
//								progress = { progressFloat },
//								color = Color.White,
//								modifier = Modifier
//									.fillMaxWidth(0.95f)
//									.height(4.dp),
//							)

									Slider(
										value = progressFloat,
										onValueChange = { },
										enabled = false,
										modifier = Modifier
											.fillMaxWidth()
											.height(4.dp),
										colors = SliderDefaults.colors(
											activeTrackColor = MaterialTheme.colorScheme.onSurface,
											inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(
												0.2f
											),
											thumbColor = MaterialTheme.colorScheme.onSurface,
											disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface,
										),
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
	)
}