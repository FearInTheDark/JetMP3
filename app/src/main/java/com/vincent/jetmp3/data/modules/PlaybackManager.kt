package com.vincent.jetmp3.data.modules

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.annotation.OptIn
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.vincent.jetmp3.data.datastore.playbackDataStore
import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.service.MusicPlaybackService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
		restoreStateFromDataStore()
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

		scope.launch { persistPlaybackState(song, true, 0L) }
	}

	fun toggle() {
		mediaController?.let { if (it.isPlaying) it.pause() else it.play() }
	}

	fun pause() {
		mediaController?.pause()
		scope.launch { persistPlaybackState(_currentSong.value!!, false, _progress.value) }
	}

	fun resume() {
		mediaController?.play()
		scope.launch { persistPlaybackState(_currentSong.value!!, true, _progress.value) }
	}

	private suspend fun persistPlaybackState(song: AudioFile, isPlaying: Boolean, progress: Long) {
		context.playbackDataStore.edit { prefs ->
			prefs[longPreferencesKey("id")] = song.id
			prefs[stringPreferencesKey("title")] = song.title
			prefs[stringPreferencesKey("artist")] = song.artist
			prefs[stringPreferencesKey("uri")] = song.uri
			prefs[longPreferencesKey("duration")] = song.duration
			prefs[booleanPreferencesKey("isPlaying")] = isPlaying
			prefs[longPreferencesKey("progress")] = progress
		}
	}

	private fun restoreStateFromDataStore() {
		scope.launch {
			val prefs = context.playbackDataStore.data.first()
			val id = prefs[longPreferencesKey("id")] ?: -1L
			val title = prefs[stringPreferencesKey("title")]
			val artist = prefs[stringPreferencesKey("artist")] ?: "Unknown"
			val uri = prefs[stringPreferencesKey("uri")]
			val duration = prefs[longPreferencesKey("duration")] ?: 0L
			val isPlaying = prefs[booleanPreferencesKey("isPlaying")] ?: false
			val progress = prefs[longPreferencesKey("progress")] ?: 0L

			if (uri != null && title != null) {
				val song = AudioFile(id, title, artist, uri, duration)
				_currentSong.value = song
				_progress.value = progress
				_isPlaying.value = isPlaying

				Log.d("PlaybackManager", "Restored song: $song")
			}
		}
	}

}
