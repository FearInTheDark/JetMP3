package com.vincent.jetmp3.media.service

import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.data.repositories.AudioRepository
import com.vincent.jetmp3.ui.viewmodels.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStateHandler @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val audioRepository: AudioRepository
) {
	private val _progress : MutableStateFlow<Long> = MutableStateFlow(0L)
	val progress = _progress.asStateFlow()

	private val _progressString : MutableStateFlow<String> = MutableStateFlow("")
	val progressString = _progressString.asStateFlow()

	private val _isPlaying : MutableStateFlow<Boolean> = MutableStateFlow(false)
	val isPlaying = _isPlaying.asStateFlow()

	private val _currentSelectedAudioFile : MutableStateFlow<AudioFile?> = MutableStateFlow(null)
	val currentSelectedAudioFile = _currentSelectedAudioFile.asStateFlow()

	private val _audioList : MutableStateFlow<List<AudioFile>> = MutableStateFlow(emptyList())
	val audioList = _audioList.asStateFlow()

	private val _uiState : MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
	val uiState = _uiState.asStateFlow()

	// Custom scope coroutine while this is not viewModel

	init {

	}

	private suspend fun getAudioData() {
		_uiState.value = UIState.Fetching

		// coroutine
		withContext(Dispatchers.IO) {
			val audioFiles = audioRepository.getLocalAudioData()
			_audioList.value = audioFiles
			_uiState.value = UIState.Ready
		}
	}



}