package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.data.repository.ServiceRepository
import com.vincent.jetmp3.data.repository.TrackRepository
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import com.vincent.jetmp3.ui.theme.TitleLineBig
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
	val tracks by recentViewModel.tracks.collectAsState()
	val uiState by recentViewModel.uiState.collectAsState()
	val scope = rememberCoroutineScope()

	Column(
		modifier = Modifier
			.fillMaxWidth(),
		horizontalAlignment = Alignment.Start,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = "History",
			style = TitleLineBig,
			fontSize = 24.sp,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.padding(horizontal = 12.dp)
		)

		LazyRow(
			contentPadding = PaddingValues(horizontal = 0.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			item { Spacer(Modifier.width(4.dp)) }
			itemsIndexed(tracks) { index, track ->
				RecentItem(track, uiState) {
					scope.launch {
						recentViewModel.prepareAndPlay(tracks, index)
					}
				}
			}
			item { Spacer(Modifier.width(4.dp)) }
		}
	}
}

@HiltViewModel
class RecentViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val trackRepository: TrackRepository,
	private val serviceRepository: ServiceRepository
) : ViewModel() {

	private val _tracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
	val tracks = _tracks.asStateFlow()

	private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Fetching)
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			_uiState.value = UIState.Fetching
			_tracks.value = trackRepository.getNestTracks() ?: emptyList()
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
		serviceRepository.startServiceRunning()
	}
}