package com.vincent.jetmp3.ui.viewmodels

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.data.repositories.AudioRepository
import com.vincent.jetmp3.service.MusicPlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
	application: Application
) : AndroidViewModel(application) {
	private val repository = AudioRepository()
	private val _audioFiles = MutableStateFlow<List<AudioFile>>(emptyList())
	val audioFiles: StateFlow<List<AudioFile>> = _audioFiles

	private val _currentSong = MutableStateFlow<AudioFile?>(null)
	val currentSong: StateFlow<AudioFile?> = _currentSong

	private val _isPlaying = MutableStateFlow(false)
	val isPlaying: StateFlow<Boolean> = _isPlaying

	private var mediaController: MediaController? = null

	init {
		fetchAudioFiles()
		initializeMediaController()
	}

	private fun fetchAudioFiles() {
		viewModelScope.launch {
			_audioFiles.value =
				repository.getAudioFiles(getApplication<Application>().contentResolver)
		}
	}

	@OptIn(UnstableApi::class)
	private fun initializeMediaController() {
		val sessionToken = SessionToken(
			getApplication(),
			ComponentName(getApplication(), MusicPlaybackService::class.java)
		)
		val controllerFuture = MediaController.Builder(getApplication(), sessionToken).buildAsync()
		controllerFuture.addListener({
			mediaController = controllerFuture.get()
			mediaController?.addListener(object : Player.Listener {
				override fun onIsPlayingChanged(isPlaying: Boolean) {
					_isPlaying.value = isPlaying
				}
			})
		}, MoreExecutors.directExecutor())
	}

	fun playSong(audioFile: AudioFile) {
		mediaController?.let { controller ->
			_currentSong.value = audioFile
			val mediaItem = MediaItem.fromUri(audioFile.uri)
			controller.setMediaItem(mediaItem)
			controller.prepare()
			controller.play()
		}
	}

	fun togglePlayPause() {
		mediaController?.let {
			if (it.isPlaying) it.pause() else it.play()
		}
	}

	fun playNext() {
		mediaController?.seekToNext()
	}

	fun playPrevious() {
		mediaController?.seekToPrevious()
	}

	override fun onCleared() {
		mediaController?.release()
		super.onCleared()
	}
}