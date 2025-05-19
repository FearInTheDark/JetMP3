package com.vincent.jetmp3.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.data.repository.TrackRepository
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import com.vincent.jetmp3.media.service.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
	private val repository: TrackRepository,
	private val mediaServiceHandler: MediaServiceHandler,
	savedStateHandle: SavedStateHandle
) : ViewModel() {

	private val playbackState = mediaServiceHandler.playbackState

	val tracks = mutableStateOf(savedStateHandle["tracks"] ?: emptyList<Track>())

	private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
	val uiState: StateFlow<UIState> get() = _uiState.asStateFlow()

	init {
		fetchTracks()
	}

	init {
		viewModelScope.launch {
			mediaServiceHandler.playerState.collectLatest { mediaState ->
				when (mediaState) {
					is PlayerState.Playing -> {
						mediaServiceHandler.updatePlaybackState { copy(isPlaying = mediaState.isPlaying) }
					}

					is PlayerState.Progress -> {
						calculateProgressValue(mediaState.progress)
						mediaServiceHandler.updatePlaybackState {
							copy(currentPosition = mediaState.progress)
						}
					}

					is PlayerState.CurrentPlaying -> {
						mediaServiceHandler.updatePlaybackState {
							copy(currentTrack = playbackState.value.queue[mediaState.mediaItemIndex])
						}
					}

					is PlayerState.Ready -> {
						_uiState.value = UIState.Ready
					}

					else -> {}
				}
			}
		}
	}

	fun fetchTracks() {
		viewModelScope.launch {
			_uiState.value = UIState.Fetching
			tracks.value = repository.getNestTracks() ?: emptyList()
			delay(3000)
			_uiState.value = UIState.Ready
		}
	}

	fun setTracks(
		tracks: List<Track> = this.tracks.value,
		index: Int = 0
	) {
		viewModelScope.launch {
			_uiState.value = UIState.FetchingTrack
			mediaServiceHandler.setMediaItemList(tracks, index)
			mediaServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
			_uiState.value = UIState.Ready
		}
	}

	override fun onCleared() {
		viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.Stop) }
		super.onCleared()
	}

	private fun calculateProgressValue(currentProgress: Long) {
		mediaServiceHandler.updatePlaybackState {
			copy(
				progress = if (currentProgress > 0)
					((currentProgress.toFloat()) / playbackState.value.duration.toFloat()) * 100f else 0f
			)
		}
	}
}
