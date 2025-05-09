package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.data.constants.UIEvent
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.repository.AudioRepository
import com.vincent.jetmp3.domain.ImagePaletteService
import com.vincent.jetmp3.domain.models.request.VibrantRequest
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import com.vincent.jetmp3.media.service.PlayerState
import com.vincent.jetmp3.utils.paletteToColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val repository: AudioRepository,
	private val imagePaletteService: ImagePaletteService,
) : ViewModel() {
	val playbackState = mediaServiceHandler.playbackState

	init {
		// Init log
		Log.d("Initialization", "AudioViewModel initialized")
	}

	private val _progressString = MutableStateFlow("00:00")
	val progressString: StateFlow<String> get() = _progressString.asStateFlow()

	private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
	val uiState: StateFlow<UIState> get() = _uiState.asStateFlow()

	init {
		getAudioData()
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

					is PlayerState.Progress -> calculateProgressValue(mediaState.progress)
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

			UIEvent.FetchAudio -> getAudioData()
		}
	}

	private fun getAudioData() {
		_uiState.value = UIState.Fetching
		viewModelScope.launch {
			mediaServiceHandler.setMediaItemList(repository.getLocalTracks())
			_uiState.value = UIState.Ready
		}
	}

	override fun onCleared() {
		viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.Stop) }
		super.onCleared()
	}

	suspend fun getDominantColor(imageUrl: String? = null): Color {
		val selectedAudio = playbackState.value.currentTrack ?: return Color.Gray

		try {
			val rgb = viewModelScope.async {
				delay(300)
				imagePaletteService.getPalette(VibrantRequest(imageUrl ?: selectedAudio.images.first()))
			}.await().muted
			Log.d("AudioViewModel", "getDominantColor: $rgb")
			return paletteToColor(rgb)
		} catch (e: Exception) {
			Log.e("AudioViewModel", "getDominantColor: ${e.message}")
			return Color.Gray
		}

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
