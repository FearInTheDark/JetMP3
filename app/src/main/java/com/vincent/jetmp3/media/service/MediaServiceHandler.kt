package com.vincent.jetmp3.media.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.vincent.jetmp3.domain.models.Track
import com.vincent.jetmp3.utils.PlaybackState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaServiceHandler @Inject constructor(
	private val exoPlayer: ExoPlayer
) : Player.Listener {
	private val _playerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.Initial)
	val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

	private val _playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(PlaybackState())
	val playbackState = _playbackState.asStateFlow()

	private var job: Job? = null
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

	init {
		exoPlayer.addListener(this)
	}

	fun setMediaItem(mediaItem: MediaItem) {
		exoPlayer.setMediaItem(mediaItem)
		exoPlayer.prepare()
		exoPlayer.play()
	}

	fun setMediaItemList(tracks: List<Track>) {
		val items = tracks.map { it.toMediaItem() }
		_playbackState.value = _playbackState.value.copy(
			queue = tracks,
			trackList = items
		)
		exoPlayer.setMediaItems(items)
		exoPlayer.prepare()
	}

	suspend fun onPlayerEvents(
		playerEvent: PlayerEvent,
		selectedAudioIndex: Int = -1,
		seekPosition: Long = 0
	) {
		when (playerEvent) {
			PlayerEvent.Backward -> exoPlayer.seekBack()
			PlayerEvent.Forward -> exoPlayer.seekForward()
			PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
			PlayerEvent.SeekToPrevious -> exoPlayer.seekToPrevious()
			PlayerEvent.PlayPause -> playOrPause()
			is PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
			is PlayerEvent.SelectedPlayerChange -> {
				when (selectedAudioIndex) {
					exoPlayer.currentMediaItemIndex -> playOrPause()
					else -> {
						exoPlayer.seekToDefaultPosition(selectedAudioIndex)
						_playerState.value = PlayerState.Playing(true)
						exoPlayer.playWhenReady = true
						startProgressUpdate()
					}
				}
			}

			is PlayerEvent.UpdateProgress -> {
				exoPlayer.seekTo((exoPlayer.duration * playerEvent.newProgress).toLong())
			}

			PlayerEvent.Stop -> stopProgressUpdate()
		}
	}

	override fun onPlaybackStateChanged(playbackState: Int) {
		when (playbackState) {
			ExoPlayer.STATE_BUFFERING -> _playerState.value = PlayerState.Buffering(exoPlayer.currentPosition)
			ExoPlayer.STATE_READY -> _playerState.value = PlayerState.Ready(exoPlayer.duration)
			ExoPlayer.STATE_ENDED -> _playerState.value = PlayerState.Ended
			else -> _playerState.value = PlayerState.Idle
		}
		updatePlaybackState()
	}

	override fun onIsPlayingChanged(isPlaying: Boolean) {
		_playerState.value = PlayerState.Playing(isPlaying = isPlaying)
		_playerState.value = PlayerState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
		if (isPlaying) {
			scope.launch {
				startProgressUpdate()
			}
		} else {
			stopProgressUpdate()
		}
		updatePlaybackState(isPlaying = isPlaying)
	}

	private suspend fun playOrPause() {
		if (exoPlayer.isPlaying) {
			exoPlayer.pause()
			stopProgressUpdate()
		} else {
			exoPlayer.play()
			_playerState.value = PlayerState.Playing(
				isPlaying = true
			)
			startProgressUpdate()
		}
	}

	private suspend fun startProgressUpdate() = job.run {
		while (true) {
			delay(500)
			_playerState.value = PlayerState.Progress(exoPlayer.currentPosition)
		}
	}

	private fun stopProgressUpdate() {
		job?.cancel()
		_playerState.value = PlayerState.Playing(
			isPlaying = false
		)
	}

	private fun updatePlaybackState(
		isPlaying: Boolean = exoPlayer.isPlaying,
		isBuffering: Boolean = exoPlayer.playbackState == ExoPlayer.STATE_BUFFERING,
		hasEnded: Boolean = exoPlayer.playbackState == ExoPlayer.STATE_ENDED
	) {
		_playbackState.value = _playbackState.value.copy(
			isPlaying = isPlaying,
			currentIndex = exoPlayer.currentMediaItemIndex,
			currentPosition = exoPlayer.currentPosition,
			duration = exoPlayer.duration,
			bufferedPosition = exoPlayer.bufferedPosition,
			trackList = exoPlayer.currentTimeline.windowCount.takeIf { it > 0 }?.let {
				(0 until it).mapNotNull { i -> exoPlayer.getMediaItemAt(i) }
			} ?: emptyList(),
			currentMediaItem = exoPlayer.currentMediaItem,
			isBuffering = isBuffering,
			hasEnded = hasEnded
		)
	}

	fun updatePlaybackState(update: PlaybackState.() -> PlaybackState) {
		_playbackState.update { it.update() }
	}
}

sealed class PlayerEvent {
	data object Stop : PlayerEvent()
	data object SeekTo : PlayerEvent()
	data object Forward : PlayerEvent()
	data object Backward : PlayerEvent()
	data object PlayPause : PlayerEvent()
	data object SeekToNext : PlayerEvent()
	data object SeekToPrevious : PlayerEvent()
	data object SelectedPlayerChange : PlayerEvent()
	data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

sealed class PlayerState {
	data object Initial : PlayerState()
	data object Ended : PlayerState()
	data object Idle : PlayerState()
	data class Ready(val duration: Long) : PlayerState()
	data class Progress(val progress: Long) : PlayerState()
	data class Buffering(val progress: Long) : PlayerState()
	data class Playing(val isPlaying: Boolean) : PlayerState()
	data class CurrentPlaying(val mediaItemIndex: Int) : PlayerState()
}