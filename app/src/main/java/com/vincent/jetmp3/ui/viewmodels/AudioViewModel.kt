package com.vincent.jetmp3.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.data.constants.UIEvent
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.data.repository.ServiceRepository
import com.vincent.jetmp3.data.repository.TrackRepository
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import com.vincent.jetmp3.media.service.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
	private val repository: TrackRepository,
	private val serviceRepository: ServiceRepository,
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
					PlayerState.Initial -> _uiState.value = UIState.Initial
					is PlayerState.Buffering -> calculateProgressValue(mediaState.progress)
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

	fun onUiEvent(uiEvent: UIEvent) = viewModelScope.launch {
		when (uiEvent) {
			UIEvent.Forward -> mediaServiceHandler.onPlayerEvents(PlayerEvent.Forward)
			UIEvent.Backward -> mediaServiceHandler.onPlayerEvents(PlayerEvent.Backward)
			UIEvent.SeekToNext -> mediaServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
			UIEvent.SeekToPrevious -> mediaServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrevious)
			UIEvent.PlayPause -> mediaServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
			is UIEvent.SelectedAudioChange -> mediaServiceHandler.onPlayerEvents(
				PlayerEvent.SelectedPlayerChange, selectedAudioIndex = uiEvent.index
			)

			is UIEvent.SeekTo -> mediaServiceHandler.onPlayerEvents(
				playerEvent = PlayerEvent.SeekTo,
				seekPosition = ((playbackState.value.duration * uiEvent.position) / 100f).toLong()
			)

			is UIEvent.UpdateProgress -> {
				mediaServiceHandler.onPlayerEvents(
					PlayerEvent.UpdateProgress(uiEvent.progress)
				)
				playbackState.value.progress = uiEvent.progress
			}

			UIEvent.FetchAudio -> fetchTracks()
		}
	}

	private fun fetchTracks() {
		_uiState.value = UIState.Fetching
		viewModelScope.launch {
			tracks.value = repository.getNestTracks() ?: emptyList()
			mediaServiceHandler.setMediaItemList(tracks.value)
			_uiState.value = UIState.Ready
		}
	}

	fun setTracks(
		tracks: List<Track> = this.tracks.value,
		index: Int = 0
	) {
		_uiState.value = UIState.Fetching
		mediaServiceHandler.setMediaItemList(tracks, index)
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

	@UnstableApi
	fun stopService() = serviceRepository.stopServiceRunning()

	@UnstableApi
	fun startService() = serviceRepository.startServiceRunning()
}
