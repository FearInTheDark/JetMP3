package com.vincent.jetmp3.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.data.modules.PlaybackManager
import com.vincent.jetmp3.data.repositories.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
	private val playbackManager: PlaybackManager,
	private val audioRepository: AudioRepository,
	application: Application
) : AndroidViewModel(application) {
	private val _audioFiles = MutableStateFlow<List<AudioFile>>(emptyList())
	val audioFiles = _audioFiles.asStateFlow()

	val currentSong = playbackManager.currentSong
	val isPlaying = playbackManager.isPlaying
	val progress = playbackManager.progress

	private val _fetching = MutableStateFlow(false)
	val fetching = _fetching.asStateFlow()

	init {
		fetchAudioFiles()
	}

	fun fetchAudioFiles() {
		_fetching.value = true
		viewModelScope.launch {
			try {
				_audioFiles.value =
					audioRepository.getAudioFiles(getApplication<Application>().contentResolver)
				Log.d("MusicViewModel", "Audio files fetched: ${_audioFiles.value.size}")
			} finally {
				_fetching.value = false
			}
		}
	}

	fun playSong(song: AudioFile) = playbackManager.playSong(song)

	fun togglePlayPause() = playbackManager.toggle()
}
