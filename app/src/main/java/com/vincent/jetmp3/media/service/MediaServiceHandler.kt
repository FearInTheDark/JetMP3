package com.vincent.jetmp3.media.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaServiceHandler @Inject constructor(
	private val exoPlayer: ExoPlayer
) : Player.Listener {
	private val _audioState: MutableStateFlow<AudioState> = MutableStateFlow(AudioState.Initial)
	val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

	private var job: Job? = null

	init {
		exoPlayer.addListener(this)
	}

	fun setMediaItem(mediaItem: MediaItem) {
		exoPlayer.setMediaItem(mediaItem)
		exoPlayer.prepare()
	}

	fun setMediaItemList(mediaItems: List<MediaItem>) {
		exoPlayer.setMediaItems(mediaItems)
		exoPlayer.prepare()
	}

	suspend fun onPlayerEvents(
		audioEvent: AudioEvent,
		selectedAudioIndex: Int = -1,
		seekPosition: Long = 0
	) {
		when (audioEvent) {
			AudioEvent.Backward -> exoPlayer.seekBack()
			AudioEvent.Forward -> exoPlayer.seekForward()
			AudioEvent.SeekToNext -> exoPlayer.seekToNext()
			AudioEvent.SeekToPrevious -> exoPlayer.seekToPrevious()
			AudioEvent.PlayPause -> playOrPause()
			is AudioEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
			is AudioEvent.SelectedAudioChange -> {
				when (selectedAudioIndex) {
					exoPlayer.currentMediaItemIndex -> playOrPause()
					else -> {
						exoPlayer.seekToDefaultPosition(selectedAudioIndex)
						_audioState.value = AudioState.Playing(true)
						exoPlayer.playWhenReady = true
						startProgressUpdate()
					}
				}
			}
			is AudioEvent.UpdateProgress -> {
				exoPlayer.seekTo(
					(exoPlayer.duration * audioEvent.newProgress).toLong()
				)
			}
			AudioEvent.Stop -> stopProgressUpdate()
		}
	}

	override fun onPlaybackStateChanged(playbackState: Int) {
		when (playbackState) {
			ExoPlayer.STATE_BUFFERING -> _audioState.value = AudioState.Buffering(exoPlayer.currentPosition)
			ExoPlayer.STATE_READY -> _audioState.value = AudioState.Ready(exoPlayer.duration)
			else -> {}
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	override fun onIsPlayingChanged(isPlaying: Boolean) {
		_audioState.value = AudioState.Playing(isPlaying = isPlaying)
		_audioState.value = AudioState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
		if (isPlaying) {
			GlobalScope.launch(Dispatchers.Main) {
				startProgressUpdate()
			}
		} else {
			stopProgressUpdate()
		}
	}

	private suspend fun playOrPause() {
		if (exoPlayer.isPlaying) {
			exoPlayer.pause()
			stopProgressUpdate()
		} else {
			exoPlayer.play()
			_audioState.value = AudioState.Playing(
				isPlaying = true
			)
			startProgressUpdate()
		}
	}

	private suspend fun startProgressUpdate() = job.run {
		while (true) {
			delay(500)
			_audioState.value = AudioState.Progress(exoPlayer.currentPosition)
		}
	}

	private fun stopProgressUpdate() {
		job?.cancel()
		_audioState.value = AudioState.Playing(
			isPlaying = false
		)
	}
}

sealed class AudioEvent {
	data object Stop : AudioEvent()
	data object SeekTo : AudioEvent()
	data object Forward : AudioEvent()
	data object Backward : AudioEvent()
	data object PlayPause : AudioEvent()
	data object SeekToNext : AudioEvent()
	data object SeekToPrevious : AudioEvent()
	data object SelectedAudioChange : AudioEvent()
	data class UpdateProgress(val newProgress: Float) : AudioEvent()
}

sealed class AudioState {
	data object Initial : AudioState()
	data class Ready(val duration: Long) : AudioState()
	data class Progress(val progress: Long) : AudioState()
	data class Buffering(val progress: Long) : AudioState()
	data class Playing(val isPlaying: Boolean) : AudioState()
	data class CurrentPlaying(val mediaItemIndex: Int) : AudioState()
}