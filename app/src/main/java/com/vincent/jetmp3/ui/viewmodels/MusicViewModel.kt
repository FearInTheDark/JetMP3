package com.vincent.jetmp3.ui.viewmodels

import android.app.Application
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

	init {
		fetchAudioFiles()
	}

	private fun fetchAudioFiles() {
		viewModelScope.launch {
			_audioFiles.value =
				audioRepository.getAudioFiles(getApplication<Application>().contentResolver)
		}
	}

	fun playSong(song: AudioFile) = playbackManager.playSong(song)
	fun togglePlayPause() = playbackManager.toggle()
}
