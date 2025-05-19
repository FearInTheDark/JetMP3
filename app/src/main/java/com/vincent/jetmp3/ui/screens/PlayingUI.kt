package com.vincent.jetmp3.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Slider
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import arrow.core.Either
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.SheetContentType
import com.vincent.jetmp3.ui.components.home.AddToPlaylistScreen
import com.vincent.jetmp3.ui.state.LocalBottomSheetState
import com.vincent.jetmp3.ui.state.LocalSheetContentType
import com.vincent.jetmp3.ui.state.LocalSnackBarHostState
import com.vincent.jetmp3.ui.theme.DarkSurface
import com.vincent.jetmp3.ui.theme.HeadLineMedium
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineSmall
import com.vincent.jetmp3.ui.viewmodels.PlayingViewModel
import com.vincent.jetmp3.utils.functions.durationToString
import com.vincent.jetmp3.utils.mixColors
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterial3Api
@ExperimentalMaterial3ExpressiveApi
fun PlayingScreen(
	viewModel: PlayingViewModel = hiltViewModel(),
	onTopAppClick: () -> Unit = {}
) {
	val scope = rememberCoroutineScope()
	val isSystemInDarkTheme = isSystemInDarkTheme()

	val playbackState by viewModel.playbackState.collectAsState()
	val progressString by remember(playbackState.currentPosition) {
		derivedStateOf {
			durationToString(playbackState.currentPosition)
		}
	}
	var showDialog by remember { mutableStateOf(false) }
	val animProgress = remember { Animatable(0f) }
	var ambientColor by remember { mutableStateOf(Color.Gray) }
	var isUserSeeking by remember { mutableStateOf(false) }
	var sliderProgress by remember { mutableFloatStateOf(0f) }

	val context = LocalContext.current
	val snackBarState = LocalSnackBarHostState.current
	val sheetContentType = LocalSheetContentType.current
	val bottomSheetScaffoldState = LocalBottomSheetState.current

	val animatedColor by animateColorAsState(
		targetValue = ambientColor,
		animationSpec = tween(durationMillis = 1000),
		label = "PlayingUI animated"
	)

	val containerColor by remember(ambientColor, animatedColor) {
		derivedStateOf {
			mixColors(
				arrayOf(
					(if (isSystemInDarkTheme) DarkSurface else Color.White) to 0.3f,
					animatedColor to 0.7f
				)
			)
		}
	}

	BackHandler {
		when (bottomSheetScaffoldState.bottomSheetState.currentValue) {
			SheetValue.PartiallyExpanded -> onTopAppClick()
			else -> scope.launch {
				bottomSheetScaffoldState.bottomSheetState.show()
			}
		}
	}

	LaunchedEffect(playbackState.currentTrack) {
		ambientColor = viewModel.getDominantColor()
	}

	LaunchedEffect(playbackState.progress) {
		animProgress.animateTo(playbackState.progress, animationSpec = tween(500))
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
			.background(
				Brush.verticalGradient(
					startY = 0f,
					endY = Float.POSITIVE_INFINITY,
					colors = listOf(
						mixColors(arrayOf(containerColor to 0.8f, MaterialTheme.colorScheme.surface to 0.2f)),
						mixColors(arrayOf(containerColor to 0.8f, MaterialTheme.colorScheme.surface to 0.4f)),
						mixColors(arrayOf(containerColor to 0.2f, MaterialTheme.colorScheme.surface to 0.8f))
					)
				)
			)
			.padding(4.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					Brush.verticalGradient(
						colors = listOf(
							mixColors(arrayOf(containerColor to 0.9f, MaterialTheme.colorScheme.surface to 0.1f)),
							mixColors(arrayOf(containerColor to 0.8f, MaterialTheme.colorScheme.surface to 0.2f)),
						)
					)
				)
		) {
			TopAppBar(
				title = {
					Text(
						text = "Playing View",
						style = HeadStyleLarge,
						color = MaterialTheme.colorScheme.onSurface
					)
				},
				navigationIcon = {
					IconButton(onClick = { onTopAppClick() }) {
						// Chevron down
						Icon(Icons.Outlined.KeyboardArrowDown, null)
					}
				},
				actions = {
					IconButton(onClick = {}) {
						Icon(Icons.Outlined.MoreVert, null)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = Color.Transparent, // Make the TopAppBar background transparent
					scrolledContainerColor = Color.Transparent,
					navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
					titleContentColor = MaterialTheme.colorScheme.onSurface,
					actionIconContentColor = MaterialTheme.colorScheme.onSurface,
				)
			)
		}

		playbackState.currentTrack?.let {
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
						model = playbackState.currentTrack?.images?.firstOrNull()
							?: "https://res.cloudinary.com/dsy29z79v/image/upload/v1746724872/music_ztrfid.jpg",
						contentDescription = "Image",
						contentScale = ContentScale.Crop,
						modifier = Modifier
							.scale(if (playbackState.isPlaying) albumScale else 1f)
							.shadow(
								elevation = 100.dp,
								clip = false,
								shape = RoundedCornerShape(10.dp),
								ambientColor = animatedColor,
								spotColor = animatedColor
							)
							.clip(RoundedCornerShape(10.dp))
							.fillMaxWidth(0.95f)
							.aspectRatio(1f)
							.combinedClickable(
								onClick = {},
								onLongClick = {
									scope.launch {
										sheetContentType.value = SheetContentType.OPTIONS
										bottomSheetScaffoldState.bottomSheetState.expand()
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
								.fillMaxWidth(0.75f)
								.padding(2.dp),
							verticalArrangement = Arrangement.Center,
							horizontalAlignment = Alignment.Start
						) {
							Text(
								text = playbackState.currentTrack!!.name,
								style = HeadLineMedium,
								maxLines = 1,
								color = MaterialTheme.colorScheme.onSurface,
								modifier = Modifier.basicMarquee()
							)

							Text(
								text = when (val artist = playbackState.currentArtist) {
									is Either.Left -> artist.value?.name ?: "Unknown Nest Artist"
									is Either.Right -> artist.value?.name ?: "Unknown Spotify Artist"
								},
								style = MaterialTheme.typography.labelMedium,
								color = MaterialTheme.colorScheme.onSurface
							)
						}

						Row(
							Modifier
								.wrapContentSize()
								.padding(2.dp),
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.CenterVertically
						) {
							IconButton(
								onClick = {
									sheetContentType.value = SheetContentType.QUEUE
									scope.launch {
										when (bottomSheetScaffoldState.bottomSheetState.currentValue) {
											SheetValue.Hidden -> bottomSheetScaffoldState.bottomSheetState.expand()
											SheetValue.PartiallyExpanded -> bottomSheetScaffoldState.bottomSheetState.expand()
											SheetValue.Expanded -> bottomSheetScaffoldState.bottomSheetState.hide()
										}
									}
								}
							) {
								Icon(
									painter = painterResource(R.drawable.heroicons__queue_list_16_solid),
									contentDescription = "Queue Toggle",
									tint = if (bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded)
										MaterialTheme.colorScheme.onSurface.copy(0.5f)
									else
										MaterialTheme.colorScheme.onSurface,
									modifier = Modifier
										.width(24.dp)
										.aspectRatio(1f)
								)
							}
							Icon(
								painter = painterResource(if (playbackState.currentTrack!!.isFavorite) R.drawable.iconoir__heart_solid else R.drawable.iconoir__heart),
								contentDescription = "Favorite",
								Modifier
									.width(24.dp)
									.aspectRatio(1f)
									.combinedClickable(
										onClick = {
											scope.launch {
												viewModel.toggleFavorite()
												val result = snackBarState.showSnackbar(
													message = if (playbackState.currentTrack!!.isFavorite) "Removed from favorites" else "Added to favorites",
													actionLabel = "Undo",
													duration = SnackbarDuration.Short,
												)
												if (result == SnackbarResult.ActionPerformed) {
													viewModel.toggleFavorite()
												}
											}
											Toast
												.makeText(
													context,
													if (playbackState.currentTrack!!.isFavorite) "Removed from favorites" else "Added to favorites",
													Toast.LENGTH_SHORT
												)
												.show()

										},
										onLongClick = { showDialog = !showDialog }
									),
								tint = if (playbackState.currentTrack!!.isFavorite) {
									Color(0xFFF64A55).copy(0.8f)
								} else {
									MaterialTheme.colorScheme.onSurface.copy(0.8f)
								},
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

							Slider(
								value = if (isUserSeeking) sliderProgress else animProgress.value,
								onValueChange = {
									sliderProgress = it
									isUserSeeking = true
								},
								onValueChangeFinished = {
									viewModel.updateProgress(sliderProgress / 100)
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
									text = progressString,
									color = MaterialTheme.colorScheme.onSurface,
									style = LabelLineSmall
								)
								Text(
									text = durationToString(playbackState.duration),
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
							horizontalArrangement = Arrangement.spacedBy(18.dp),
							verticalAlignment = Alignment.CenterVertically,
						) {
							IconButton(
								onClick = { viewModel.seekToPrevious() }
							) {
								Icon(
									painter = painterResource(R.drawable.mage__previous_fill),
									contentDescription = "previous",
									modifier = Modifier.size(32.dp),
									tint = Color.Unspecified
								)
							}
//									IconButton(
//										onClick = { viewModel.backward() }
//									) {
//										Icon(
//											painter = painterResource(R.drawable.iconoir__rewind_solid),
//											contentDescription = "previous",
//											modifier = Modifier.size(32.dp),
//										)
//									}
							IconButton(
								onClick = { viewModel.playOrPause() },
								modifier = Modifier
									.size(80.dp)
									.background(MaterialTheme.colorScheme.onSurface, CircleShape),
								enabled = !playbackState.isBuffering
							) {
								if (playbackState.isBuffering) LoadingIndicator(
									modifier = Modifier.size(44.dp),
									color = MaterialTheme.colorScheme.tertiaryContainer,
								) else
									Icon(
										painter = painterResource(
											if (playbackState.isPlaying) R.drawable.solar__pause_bold
											else R.drawable.solar__play_bold
										),
										contentDescription = "play",
										modifier = Modifier.size(32.dp),
										tint = MaterialTheme.colorScheme.surface
									)
							}
//									IconButton(
//										onClick = { viewModel.forward() }
//									) {
//										Icon(
//											painter = painterResource(R.drawable.iconoir__forward_solid),
//											contentDescription = "next",
//											modifier = Modifier.size(32.dp),
//										)
//									}
							IconButton(
								onClick = { viewModel.seekToNext() }
							) {
								Icon(
									painter = painterResource(R.drawable.mage__next_fill),
									contentDescription = "next",
									modifier = Modifier.size(32.dp),
									tint = Color.Unspecified
								)
							}
						}
					}
				}
			}
			if (showDialog) {
				Dialog(
					onDismissRequest = { showDialog = false }
				) {
					AddToPlaylistScreen(
						currentTrackId = it.id,
						trackName = it.name,
						onBackClick = { showDialog = false }
					)
				}
			}
		}
	}
}