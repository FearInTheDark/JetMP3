package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.data.constants.TrackSelectType
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.data.repository.NestRepository
import com.vincent.jetmp3.data.repository.TrackRepository
import com.vincent.jetmp3.domain.models.CategoryScreenData
import com.vincent.jetmp3.domain.models.RecentCategoryItem
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import com.vincent.jetmp3.ui.theme.HeadLineMedium
import com.vincent.jetmp3.utils.functions.shimmerBackground
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@Composable
fun RecentScroll(
	recentViewModel: RecentViewModel = hiltViewModel()
) {
	val data by recentViewModel.screenData.collectAsState()
	val uiState by recentViewModel.uiState.collectAsState()
	val scope = rememberCoroutineScope()

	LaunchedEffect(Unit) {
		recentViewModel.fetch()
	}

	when (uiState) {
		UIState.Ready -> @Composable {
			data.shuffled().forEachIndexed { _, screenData ->

				Column(
					modifier = Modifier
						.fillMaxWidth(),
					horizontalAlignment = Alignment.Start,
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					if (screenData?.data?.tracks?.isNotEmpty() == true)
						Text(
							text = screenData.description ?: screenData.title,
							style = HeadLineMedium,
							fontSize = 24.sp,
							color = MaterialTheme.colorScheme.onSurface,
							modifier = Modifier.padding(horizontal = 12.dp)
						)

					LazyRow(
						contentPadding = PaddingValues(horizontal = 0.dp),
						horizontalArrangement = Arrangement.spacedBy(8.dp),
					) {
						item { Spacer(Modifier.width(4.dp)) }
						itemsIndexed(screenData?.data?.tracks!!) { index, track ->
							TrackSelect(
								track = track,
								type = TrackSelectType.GRID,
								boxSize = if (screenData.title == "My History Tracks") 140.dp else 160.dp
								) {
								scope.launch {
									recentViewModel.prepareAndPlay(screenData.data.tracks, index)
								}
							}
						}
						item { Spacer(Modifier.width(4.dp)) }
					}
				}

				Spacer(Modifier.height(24.dp))
			}
		}

		UIState.Fetching -> {
			Column(
				modifier = Modifier
					.fillMaxWidth(),
				horizontalAlignment = Alignment.Start,
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				LazyRow(
					contentPadding = PaddingValues(horizontal = 0.dp),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
				) {
					item { Spacer(Modifier.width(4.dp)) }
					items(4) { _ ->
						Box(
							Modifier
								.size(160.dp)
								.clip(RoundedCornerShape(8.dp))
								.shimmerBackground()
						)
					}
					item { Spacer(Modifier.width(4.dp)) }
				}
			}
		}

		else -> {}
	}
}

@HiltViewModel
class RecentViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val trackRepository: TrackRepository,
	private val nestRepository: NestRepository
) : ViewModel() {

	private val _categories : MutableStateFlow<RecentCategoryItem?> = MutableStateFlow(null)

	private val _screenData: MutableStateFlow<MutableList<CategoryScreenData?>> = MutableStateFlow(mutableListOf())
	val screenData = _screenData.asStateFlow()

	private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Fetching)
	val uiState = _uiState.asStateFlow()

	init {
		_uiState.value = UIState.Fetching
	}

	fun fetch() {
		viewModelScope.launch {
			_uiState.value = UIState.Fetching
			_categories.value = nestRepository.getRecentCategories()
			_screenData.value.clear()
			_screenData.value.add(trackRepository.getFavoriteTracks())
			_screenData.value.add(trackRepository.getHistoryTracks())
			_categories.value?.playlists?.forEach {
				_screenData.value.add(nestRepository.getCategoryData(it.url))
			}
			_screenData.value.shuffle()
			_uiState.value = UIState.Ready
		}
	}

	@UnstableApi
	suspend fun prepareAndPlay(
		tracks: List<Track> = emptyList(),
		index: Int = 0
	) {
		mediaServiceHandler.setMediaItemList(tracks, index)
		mediaServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
	}
}