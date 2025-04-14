package com.vincent.jetmp3.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.theme.HeadLineMedium
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineSmall
import com.vincent.jetmp3.ui.viewmodels.AudioViewModel
import com.vincent.jetmp3.ui.viewmodels.UIEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlayingScreen(
	viewModel: AudioViewModel = hiltViewModel(),
	onTopAppClick: () -> Unit = {}
) {

	val currentSong = viewModel.currentSelectedAudio
	val isPlaying = viewModel.isPlaying
	val progress = viewModel.progress
	val progressString = viewModel.progressString
	var sliderProgress by remember { mutableFloatStateOf(0f) }
	var isUserSeeking by remember { mutableStateOf(false) }

	val standardBottomSheet = rememberStandardBottomSheetState(
		initialValue = SheetValue.Hidden,
		skipHiddenState = false,
	)
	val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
		bottomSheetState = standardBottomSheet,
	)
	val scope = rememberCoroutineScope()

	val animProgress = remember { Animatable(0f) }

	LaunchedEffect(progress) {
		animProgress.animateTo(progress, animationSpec = tween(500))
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
		sheetPeekHeight = 36.dp,
		containerColor = MaterialTheme.colorScheme.surface,
		content = @Composable { innerPadding ->
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
									.combinedClickable(
										onClick = {},
										onLongClick = {
											scope.launch {
												when (bottomSheetScaffoldState.bottomSheetState.currentValue) {
													SheetValue.Hidden -> bottomSheetScaffoldState.bottomSheetState.expand()
													SheetValue.PartiallyExpanded -> bottomSheetScaffoldState.bottomSheetState.expand()
													SheetValue.Expanded -> bottomSheetScaffoldState.bottomSheetState.hide()
												}
											}
										}
									)

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
										.fillMaxWidth(0.7f)
										.wrapContentSize()
										.padding(2.dp),
									verticalArrangement = Arrangement.Center,
									horizontalAlignment = Alignment.Start
								) {
									Text(
										text = currentSong.title,
										style = HeadLineMedium,
										color = MaterialTheme.colorScheme.onSurface,
										modifier = Modifier.basicMarquee()
									)

									Text(
										text = currentSong.artist,
										style = MaterialTheme.typography.labelMedium,
										color = MaterialTheme.colorScheme.onSurface
									)
								}

								Column(
									Modifier
										.wrapContentSize()
										.padding(2.dp),
									verticalArrangement = Arrangement.Center,
									horizontalAlignment = Alignment.End
								) {
									Text(
										text = progress.toString(),
										style = MaterialTheme.typography.labelMedium,
										color = MaterialTheme.colorScheme.onSurface
									)
									Text(
										text = bottomSheetScaffoldState.bottomSheetState.currentValue.toString(),
										style = MaterialTheme.typography.labelMedium,
										color = MaterialTheme.colorScheme.onSurface,
										overflow = TextOverflow.Ellipsis,
										maxLines = 1,
										softWrap = false,
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
										value = if (isUserSeeking) sliderProgress else progress,
										onValueChange = {
											sliderProgress = it
											isUserSeeking = true
										},
										onValueChangeFinished = {
											viewModel.onUiEvent(UIEvent.UpdateProgress(sliderProgress / 100))
											isUserSeeking = false
										},
										valueRange = 0f..100f,
										enabled = true,
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
											text = progressString,
											color = MaterialTheme.colorScheme.onSurface,
											style = LabelLineSmall
										)
									}
								}
							}

							Box(
								Modifier.fillMaxWidth(),
								contentAlignment = Alignment.Center
							) {
								Row(
									horizontalArrangement = Arrangement.spacedBy(28.dp),
									verticalAlignment = Alignment.CenterVertically,
								) {
									IconButton(
										onClick = { viewModel.onUiEvent(UIEvent.SeekToPrevious) }
									) {
										Icon(
											painter = painterResource(R.drawable.mage__previous_fill),
											contentDescription = "previous",
											modifier = Modifier.size(32.dp),
										)
									}
									Box {
										IconButton(
											onClick = {
												viewModel.onUiEvent(UIEvent.PlayPause)
											},
											modifier = Modifier
												.size(80.dp)
												.background(MaterialTheme.colorScheme.onSurface, CircleShape)
										) {
											Icon(
												painter = painterResource(
													if (isPlaying) R.drawable.solar__pause_bold
													else R.drawable.solar__play_bold
												),
												contentDescription = "play",
												modifier = Modifier.size(32.dp),
												tint = MaterialTheme.colorScheme.surface
											)
										}
									}
									IconButton(
										onClick = { viewModel.onUiEvent(UIEvent.SeekToNext) },
									) {
										Icon(
											painter = painterResource(R.drawable.mage__next_fill),
											contentDescription = "next",
											modifier = Modifier.size(32.dp),
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