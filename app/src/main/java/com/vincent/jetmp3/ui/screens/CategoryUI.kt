package com.vincent.jetmp3.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.data.repository.NestRepository
import com.vincent.jetmp3.data.repository.TrackRepository
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.ui.components.home.TrackSelect
import com.vincent.jetmp3.ui.components.image.StackedImages
import com.vincent.jetmp3.ui.layout.LoadingOverlay
import com.vincent.jetmp3.ui.theme.TitleLineBig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
	show: Boolean,
	viewModel: CategoryViewModel = hiltViewModel(),
	onDismiss: () -> Unit = {},
) {
	val scope = rememberCoroutineScope()
	val screenHeight = LocalConfiguration.current.screenHeightDp.dp
	val cloudTracks by viewModel.cloudTracks.collectAsState()
	val uiState by viewModel.uiState.collectAsState()
	val visibleItems = remember { mutableStateListOf<Boolean>() }
	var background by remember { mutableStateOf(Color.Gray) }
	var imageUrls by remember { mutableStateOf(emptyList<String>()) }

	// Launch animations when tracks are loaded
	LaunchedEffect(cloudTracks) {
		visibleItems.clear()
		cloudTracks.forEachIndexed { _, _ ->
			visibleItems.add(true)
			delay(100)
		}
	}

	LaunchedEffect(uiState) {
		if (uiState == UIState.Ready) {
			imageUrls = cloudTracks.shuffled().take(4).mapNotNull { it.images.firstOrNull() }
			background = viewModel.getDominantColor(imageUrls.first())
		}
	}

	val animatedColor by animateColorAsState(
		targetValue = background,
		animationSpec = tween(durationMillis = 1000),
		label = "PlayingUI animated"
	)

	AnimatedVisibility(
		visible = show,
		enter = expandVertically {
			(screenHeight.value * 0.2).toInt()
		},
		exit = shrinkVertically {
			(screenHeight.value * 0.2).toInt()
		}
	) {
		Box(
			Modifier.fillMaxSize()
		) {
			TopAppBar(
				title = {},
				navigationIcon = {
					IconButton(onClick = { onDismiss() }) {
						Icon(imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft, contentDescription = null)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = Color.Transparent
				),
				modifier = Modifier.zIndex(1f)
			)

			when (uiState) {
				UIState.Fetching -> @Composable {
					LoadingOverlay(
						isLoading = show,
						iconModifier = Modifier.size(300.dp),
						icon = "music.lottie"
					)
				}

				UIState.Ready -> @Composable {
					LazyColumn {
						item {
							Box(
								Modifier
									.fillMaxWidth()
									.background(
										Brush.verticalGradient(
											0.0f to animatedColor,
											1.0f to MaterialTheme.colorScheme.surface
										)
									),
								contentAlignment = Alignment.Center
							) {
								Column(
									modifier = Modifier
										.fillMaxSize()
										.offset(y = 24.dp)
										.padding(vertical = 36.dp, horizontal = 8.dp),
									horizontalAlignment = Alignment.CenterHorizontally,
									verticalArrangement = Arrangement.Center
								) {
									if (cloudTracks.isNotEmpty() && cloudTracks.first().images.isNotEmpty()) {
										StackedImages(
											imageUrls = imageUrls,
										)
									}

									Row(
										modifier = Modifier
											.fillMaxWidth()
											.padding(horizontal = 8.dp),
										verticalAlignment = Alignment.CenterVertically,
										horizontalArrangement = Arrangement.SpaceBetween
									) {
										Row(
											horizontalArrangement = Arrangement.spacedBy(8.dp)
										) {
											AsyncImage(
												model = "https://misc.scdn.co/liked-songs/liked-songs-640.jpg",
												contentDescription = "Favorite",
												contentScale = ContentScale.Crop,
												modifier = Modifier
													.size(36.dp)
													.aspectRatio(1f)
													.clip(RoundedCornerShape(4.dp))
											)
											Text(
												text = "Favorite Tracks",
												style = TitleLineBig,
												fontSize = 24.sp,
												color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
											)
										}

										Row {
											IconButton(
												onClick = {},
												modifier = Modifier
													.size(50.dp)
													.background(MaterialTheme.colorScheme.tertiary.copy(0.8f), CircleShape)
											) {
												Icon(
													painter = painterResource(R.drawable.solar__play_bold),
													contentDescription = null,
													tint = MaterialTheme.colorScheme.onSurface.copy(0.8f)
												)
											}
										}
									}

									Row(
										Modifier.fillMaxWidth(),
//									.padding(horizontal = 8.dp)
										horizontalArrangement = Arrangement.spacedBy(8.dp)
									) {
										IconButton(onClick = {}) {
											Icon(
												painter = painterResource(R.drawable.iconoir__heart),
												contentDescription = null,
												tint = Color.Unspecified
											)
										}

										IconButton(
											onClick = {},
										) {
											Icon(
												imageVector = Icons.Filled.MoreVert,
												contentDescription = "More",
												tint = MaterialTheme.colorScheme.onSurface
											)
										}
									}
								}
							}
						}

						itemsIndexed(cloudTracks) { index, track ->
							AnimatedVisibility(
								visible = visibleItems.getOrNull(index) == true,
								enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
								exit = fadeOut()
							) {
								TrackSelect(track)
							}
						}
					}
				}

				else -> @Composable {
				}
			}
		}
	}
}

@HiltViewModel
class CategoryViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val trackRepository: TrackRepository,
	private val nestRepository: NestRepository
) : ViewModel() {
	private val _cloudTracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
	val cloudTracks = _cloudTracks.asStateFlow()

	private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Fetching)
	val uiState = _uiState.asStateFlow()

	private val playbackState = mediaServiceHandler.playbackState

	init {
		viewModelScope.launch {
			_cloudTracks.value = trackRepository.getNestTracks() ?: emptyList()
			_uiState.value = UIState.Ready
		}
	}

	suspend fun getDominantColor(imageUrl: String = playbackState.value.currentTrack?.images?.firstOrNull() ?: ""): Color =
		nestRepository.getDominantColor(imageUrl)
}