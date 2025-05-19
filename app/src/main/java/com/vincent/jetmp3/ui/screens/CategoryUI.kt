package com.vincent.jetmp3.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.SheetContentType
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.repository.NestRepository
import com.vincent.jetmp3.data.repository.ServiceRepository
import com.vincent.jetmp3.domain.models.CategoryScreenData
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import com.vincent.jetmp3.ui.components.home.TrackSelect
import com.vincent.jetmp3.ui.components.image.StackedImages
import com.vincent.jetmp3.ui.layout.LoadingOverlay
import com.vincent.jetmp3.ui.state.LocalBottomSheetState
import com.vincent.jetmp3.ui.state.LocalSelectedCategory
import com.vincent.jetmp3.ui.state.LocalSelectedTrack
import com.vincent.jetmp3.ui.state.LocalSheetContentType
import com.vincent.jetmp3.ui.theme.HeadLineMedium
import com.vincent.jetmp3.ui.theme.LabelLineSmall
import com.vincent.jetmp3.ui.theme.TitleLineBig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
@UnstableApi
@ExperimentalMaterial3Api
fun CategoryScreen(
	show: Boolean,
	viewModel: CategoryViewModel = hiltViewModel(),
	onDismiss: () -> Unit = {},
) {
	val scope = rememberCoroutineScope()
	val selectedTrack = LocalSelectedTrack.current
	val selectedCategory = LocalSelectedCategory.current
	val sheetContentType = LocalSheetContentType.current
	val bottomSheetScaffoldState = LocalBottomSheetState.current

	val screenData by viewModel.screenData.collectAsState()
	val playbackState by viewModel.playbackState.collectAsState()
	val uiState by viewModel.uiState.collectAsState()

	val visibleItems = remember { mutableStateListOf<Boolean>() }
	var background by remember { mutableStateOf(Color(0xFF7062f0)) }
	var imageUrls by remember { mutableStateOf(emptyList<String>()) }

	// Launch animations when tracks are loaded
	LaunchedEffect(screenData?.data?.tracks) {
		visibleItems.clear()
		screenData?.data?.tracks?.forEachIndexed { _, _ ->
			visibleItems.add(true)
			delay(100)
		}
	}

	LaunchedEffect(selectedCategory.value) {
		viewModel.fetchCategoryData(selectedCategory.value)
	}

	LaunchedEffect(uiState) {
		if (uiState == UIState.Ready) {
			screenData?.let {
				imageUrls = screenData!!.data.tracks.shuffled().take(4).mapNotNull { it.images.firstOrNull() }
				background = viewModel.getDominantColor(imageUrls.firstOrNull())
			}
		}
	}

	val animatedColor by animateColorAsState(
		targetValue = background,
		animationSpec = tween(durationMillis = 1000),
		label = "PlayingUI animated"
	)

	BackHandler { onDismiss() }

	AnimatedVisibility(
		visible = show,
		enter = slideInVertically(
			initialOffsetY = { fullHeight -> fullHeight },
			animationSpec = tween(200),
		) + fadeIn(
			animationSpec = tween(durationMillis = 200),
		),
		exit = slideOutVertically(
			targetOffsetY = { fullHeight -> fullHeight },
			animationSpec = tween(200)
		) + fadeOut(
			animationSpec = tween(durationMillis = 200),
		)
	) {
		Box(
			Modifier
				.fillMaxSize()
				.zIndex(5f)
				.background(MaterialTheme.colorScheme.surface)
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
					PullToRefreshBox(
						isRefreshing = uiState == UIState.Fetching,
						onRefresh = { viewModel.fetchCategoryData(selectedCategory.value) }
					) {
						LazyColumn(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							item {
								Box(
									modifier = Modifier
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
										if (screenData?.data?.tracks?.isNotEmpty() == true) {
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
												horizontalArrangement = Arrangement.spacedBy(8.dp),
												verticalAlignment = Alignment.CenterVertically
											) {
												AsyncImage(
													model = screenData?.thumbnailUri,
													fallback = painterResource(R.drawable.hugeicons__playlist_01),
													contentDescription = "Favorite",
													contentScale = ContentScale.Crop,
													modifier = Modifier
														.size(48.dp)
														.aspectRatio(1f)
														.clip(RoundedCornerShape(4.dp))
												)
												Column {
													Text(
														text = screenData?.title ?: "Category",
														style = TitleLineBig,
														fontSize = 24.sp,
														color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
													)
													screenData?.description?.let {
														Text(
															text = it,
															style = LabelLineSmall,
															fontSize = 12.sp,
															color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
														)
													}
												}
											}

											Row {
												IconButton(
													onClick = {
														viewModel.prepareAndPlay()
														viewModel.startService()
													},
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
											horizontalArrangement = Arrangement.spacedBy(8.dp),
											verticalAlignment = Alignment.CenterVertically
										) {
											IconButton(onClick = {}) {
												Icon(
													painter = painterResource(R.drawable.iconoir__heart),
													contentDescription = null,
													tint = Color.Unspecified
												)
											}
											IconButton(
												onClick = {
													viewModel.switchShuffleMode()
												},
												modifier = Modifier
													.size(36.dp)
											) {
												Icon(
													painter = painterResource(R.drawable.famicons__shuffle),
													contentDescription = null,
													tint = if (playbackState.isShuffleMode)
														Color.Green.copy(0.8f)
													else
														MaterialTheme.colorScheme.onSurface.copy(0.8f)
												)
											}

											IconButton(
												onClick = {
													scope.launch {
														sheetContentType.value = SheetContentType.CATEGORY_OPTION
														bottomSheetScaffoldState.bottomSheetState.expand()
													}
												},
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

							if (screenData?.data?.tracks?.isEmpty() == true) {
								item {
									LoadingOverlay(
										isLoading = true,
										backgroundColor = Color.Transparent,
										icon = "empty.lottie",
										iconModifier = Modifier
											.size(400.dp)
									)
									Text(
										text = "No Tracks",
										style = HeadLineMedium,
										color = MaterialTheme.colorScheme.onSurface
									)
								}
							}

							itemsIndexed(screenData?.data?.tracks ?: emptyList()) { index, track ->
								AnimatedVisibility(
									visible = visibleItems.getOrNull(index) == true,
									enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
									exit = fadeOut()
								) {
									TrackSelect(track,
										onOptionClick = {
											scope.launch {
												selectedTrack.value = track
												sheetContentType.value = SheetContentType.OPTIONS
												bottomSheetScaffoldState.bottomSheetState.expand()
											}
										}) {
										viewModel.prepareAndPlay(index)
										viewModel.startService()
									}
								}
							}

							item {
								Spacer(Modifier.height(300.dp))
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
	private val nestRepository: NestRepository,
	private val serviceRepository: ServiceRepository,
) : ViewModel() {

	private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Fetching)
	val uiState = _uiState.asStateFlow()

	private val _screenData: MutableStateFlow<CategoryScreenData?> = MutableStateFlow(null)
	val screenData = _screenData.asStateFlow()

	val playbackState = mediaServiceHandler.playbackState

	init {
		viewModelScope.launch {
			screenData.collect { data ->
				data?.let {
					Log.d("RecentCategoryViewModel", "Screen data changed: $data")
				}
			}
		}
	}

	suspend fun getDominantColor(imageUrl: String? = playbackState.value.currentTrack?.images?.firstOrNull() ?: ""): Color =
		nestRepository.getDominantColor(imageUrl)

	fun fetchCategoryData(path: String) = viewModelScope.launch {
		_uiState.value = UIState.Fetching
		_screenData.value = nestRepository.getCategoryData(path)
		_uiState.value = UIState.Ready
	}

	fun prepareAndPlay(index: Int = 0) {
		viewModelScope.launch {
			_screenData.value?.data?.tracks?.let {
				mediaServiceHandler.setMediaItemList(it, index)
				mediaServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
			}
		}
	}

	fun switchShuffleMode() {
		mediaServiceHandler.switchShuffleMode()
	}

	@UnstableApi
	fun startService() = serviceRepository.startServiceRunning()
}