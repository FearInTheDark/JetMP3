package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.data.repositories.AudioRepository
import com.vincent.jetmp3.domain.ImagePaletteService
import com.vincent.jetmp3.domain.models.Track
import com.vincent.jetmp3.domain.models.request.VibrantRequest
import com.vincent.jetmp3.media.service.PlayerEvent
import com.vincent.jetmp3.media.service.PlayerState
import com.vincent.jetmp3.media.service.MediaServiceHandler
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
import kotlin.math.log

@HiltViewModel
class AudioViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val repository: AudioRepository,
	private val imagePaletteService: ImagePaletteService,
) : ViewModel() {
	private val _duration = MutableStateFlow(0L)

	private val _progress = MutableStateFlow(0f)
	val progress: StateFlow<Float> get() = _progress.asStateFlow()

	private val _progressString = MutableStateFlow("00:00")
	val progressString: StateFlow<String> get() = _progressString.asStateFlow()

	private val _isPlaying = MutableStateFlow(false)
	val isPlaying: StateFlow<Boolean> get() = _isPlaying.asStateFlow()

	private val _currentSelectedAudio = MutableStateFlow<AudioFile?>(null)
	val currentSelectedAudio: StateFlow<AudioFile?> get() = _currentSelectedAudio.asStateFlow()

	private val _localAudioList = MutableStateFlow<List<AudioFile>>(emptyList())
	val localAudioList: StateFlow<List<AudioFile>> get() = _localAudioList.asStateFlow()

	private val _cloudTracks = MutableStateFlow<List<Track>?>(emptyList())
	val cloudTracks: StateFlow<List<Track>?> get() = _cloudTracks.asStateFlow()

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
						_isPlaying.value = mediaState.isPlaying
						_uiState.value = if (isPlaying.value) UIState.Playing else UIState.Pausing
					}

					is PlayerState.Progress -> calculateProgressValue(mediaState.progress)
					is PlayerState.CurrentPlaying -> {
						_currentSelectedAudio.value = _localAudioList.value[mediaState.mediaItemIndex]
					}

					is PlayerState.Ready -> {
						_duration.value = mediaState.duration
						_uiState.value = UIState.Ready
					}

					else -> {}
				}
				Log.d("AudioViewModel", "playerState: $mediaState")
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
				seekPosition = ((_duration.value * uiEvent.position) / 100f).toLong()
			)

			is UIEvent.UpdateProgress -> {
				mediaServiceHandler.onPlayerEvents(
					PlayerEvent.UpdateProgress(uiEvent.progress)
				)
				_progress.value = uiEvent.progress
			}

			UIEvent.FetchAudio -> getAudioData()
		}
	}

	private fun getAudioData() {
		_uiState.value = UIState.Fetching
		viewModelScope.launch {
			_localAudioList.value = repository.getLocalAudioData()
			_cloudTracks.value = repository.getNestAudioData()
			Log.d("AudioViewModel", "_cloudTracks: ${_cloudTracks.value}")
			setMediaItems()
			_uiState.value = UIState.Ready
		}
	}

	private fun setMediaItems() {
		_localAudioList.value.map { audio ->
			MediaItem.Builder().setUri(audio.uri).setMediaMetadata(
				MediaMetadata.Builder()
					.setAlbumArtist(audio.artist)
					.setDisplayTitle(audio.displayName)
					.setSubtitle(audio.displayName).build()
			).build()
		}.also {
			mediaServiceHandler.setMediaItemList(it)
		}
	}

	override fun onCleared() {
		viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.Stop) }
		super.onCleared()
	}

	suspend fun getDominantColor(imageUrl: String? = null): Color {
		val selectedAudio = _currentSelectedAudio.value ?: return Color.Gray

		try {
			val rgb = viewModelScope.async {
				delay(300)
				imagePaletteService.getPalette(VibrantRequest(imageUrl ?: selectedAudio.imageSource))
			}.await().muted
			Log.d("TAG", "getDominantColor: $rgb")
			return paletteToColor(rgb)
		} catch (e: Exception) {
			Log.e("TAG", "getDominantColor: ${e.message}")
			return Color.Gray
		}

	}

	private fun calculateProgressValue(currentProgress: Long) {
		_progress.value = if (currentProgress > 0) ((currentProgress.toFloat()) / _duration.value.toFloat()) * 100f else 0f
		progressString
	}
}

sealed class UIEvent {
	data object Forward : UIEvent()
	data object Backward : UIEvent()
	data object PlayPause : UIEvent()
	data object SeekToNext : UIEvent()
	data object SeekToPrevious : UIEvent()
	data object FetchAudio : UIEvent()
	data class SeekTo(val position: Float) : UIEvent()
	data class UpdateProgress(val progress: Float) : UIEvent()
	data class SelectedAudioChange(val index: Int) : UIEvent()
}

sealed class UIState {
	data object Initial : UIState()
	data object Ready : UIState()
	data object Playing : UIState()
	data object Pausing : UIState()
	data object Fetching : UIState()
}