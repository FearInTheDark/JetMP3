package com.vincent.jetmp3.ui.viewmodels

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.data.repositories.AudioRepository
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.AudioState
import com.vincent.jetmp3.media.service.AudioEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class AudioViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val repository: AudioRepository,
	savedStateHandle: SavedStateHandle
) : ViewModel() {
	var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
	var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
	var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
	var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
	var currentSelectedAudio by savedStateHandle.saveable { mutableStateOf<AudioFile?>(null) }
	var audioList by savedStateHandle.saveable { mutableStateOf(listOf<AudioFile>()) }

	private val _uiState : MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
	val uiState = _uiState.asStateFlow()

	init {
		getAudioData()
	}

	init {
		viewModelScope.launch {
			mediaServiceHandler.audioState.collectLatest { mediaState->
				when    (mediaState) {
					AudioState.Initial -> _uiState.value = UIState.Initial
					is AudioState.Buffering -> calculateProgressValue(mediaState.progress)
					is AudioState.Playing -> isPlaying = mediaState.isPlaying
					is AudioState.Progress -> calculateProgressValue(mediaState.progress)
					is AudioState.CurrentPlaying -> {
						currentSelectedAudio= audioList[mediaState.mediaItemIndex]
					}
					is AudioState.Ready  -> {
						duration = mediaState.duration
						_uiState.value = UIState.Ready
					}
				}
			}
		}
	}

	fun onUiEvent(uiEvent: UIEvent) = viewModelScope.launch {
		when (uiEvent) {
			UIEvent.Forward -> mediaServiceHandler.onPlayerEvents(AudioEvent.Forward)
			UIEvent.Backward -> mediaServiceHandler.onPlayerEvents(AudioEvent.Backward)
			UIEvent.SeekToNext -> mediaServiceHandler.onPlayerEvents(AudioEvent.SeekToNext)
			UIEvent.SeekToPrevious -> mediaServiceHandler.onPlayerEvents(AudioEvent.SeekToPrevious)
			is UIEvent.PlayPause -> mediaServiceHandler.onPlayerEvents(AudioEvent.PlayPause)
			is UIEvent.SelectedAudioChange -> mediaServiceHandler.onPlayerEvents(
					AudioEvent.SelectedAudioChange,
					selectedAudioIndex = uiEvent.index
				)
			is UIEvent.SeekTo -> mediaServiceHandler.onPlayerEvents(
					audioEvent =  AudioEvent.SeekTo,
					seekPosition = ((duration * uiEvent.position) / 100f).toLong())

			is UIEvent.UpdateProgress -> {
				mediaServiceHandler.onPlayerEvents(
					AudioEvent.UpdateProgress(uiEvent.progress)
				)
				progress = uiEvent.progress
			}
			is UIEvent.FetchAudio -> getAudioData()
		}
	}

	private fun getAudioData() {
		_uiState.value = UIState.Fetching
		viewModelScope.launch {
			val audio = repository.getAudioData()
			audioList = audio
			setMediaItems()
			_uiState.value = UIState.Ready
		}
	}

	private fun setMediaItems() {
		audioList.map { audio ->
			MediaItem.Builder()
				.setUri(audio.uri)
				.setMediaMetadata(MediaMetadata.Builder()
					.setAlbumArtist(audio.artist)
					.setDisplayTitle(audio.displayName)
					.setSubtitle(audio.displayName)
					.build())
				.build()
		}.also {
			mediaServiceHandler.setMediaItemList(it)
		}
	}

	override fun onCleared() {
		viewModelScope.launch { mediaServiceHandler.onPlayerEvents(AudioEvent.Stop) }
		super.onCleared()
	}
	
	private fun calculateProgressValue(currentProgress: Long) {
		progress = if (currentProgress > 0) ((currentProgress.toFloat()) /  duration.toFloat()) * 100f else 0f
		progressString
	}
}

sealed class UIEvent {
	data object Forward: UIEvent()
	data object Backward: UIEvent()
	data object PlayPause: UIEvent()
	data object SeekToNext: UIEvent()
	data object SeekToPrevious: UIEvent()
	data object FetchAudio: UIEvent()
	data class SeekTo(val position: Float): UIEvent()
	data class UpdateProgress(val progress: Float): UIEvent()
	data class SelectedAudioChange(val index: Int): UIEvent()
}


sealed class UIState {
	data object Initial : UIState()
	data object Ready : UIState()
	data object Fetching : UIState()
}