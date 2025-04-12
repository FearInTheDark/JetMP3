package com.vincent.jetmp3.data.modules

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.service.MusicPlaybackService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
	private val context: Application,
) {
	private val _currentSong = MutableStateFlow<AudioFile?>(null)
	val currentSong: StateFlow<AudioFile?> = _currentSong

	private val _isPlaying = MutableStateFlow(false)
	val isPlaying: StateFlow<Boolean> = _isPlaying

	private val _progress = MutableStateFlow(0L)
	val progress: StateFlow<Long> = _progress

	private var mediaController: MediaController? = null
	private var progressJob: Job? = null

	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

	init {
		initMediaController()
	}

	@OptIn(UnstableApi::class)
	private fun initMediaController() {
		val token = SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
		val future = MediaController.Builder(context, token).buildAsync()
		future.addListener({
			mediaController = future.get()
			mediaController?.addListener(object : Player.Listener {
				override fun onIsPlayingChanged(isPlaying: Boolean) {
					_isPlaying.value = isPlaying
					if (isPlaying) startTrackingProgress()
					else stopTrackingProgress()
				}
			})
		}, MoreExecutors.directExecutor())
	}

	private fun startTrackingProgress() {
		if (progressJob?.isActive == true) return

		progressJob = scope.launch {
			while (true) {
				mediaController?.let {
					_progress.value = it.currentPosition
				}
				delay(1000)
			}
		}
	}

	private fun stopTrackingProgress() {
		progressJob?.cancel()
		progressJob = null
	}

	fun playSong(song: AudioFile) {
		_currentSong.value = song
		mediaController?.setMediaItem(MediaItem.fromUri(song.uri))
		mediaController?.prepare()
		mediaController?.play()
	}

	fun toggle() {
		mediaController?.let { if (it.isPlaying) it.pause() else it.play() }
	}
}
