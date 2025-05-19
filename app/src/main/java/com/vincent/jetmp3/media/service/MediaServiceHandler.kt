package com.vincent.jetmp3.media.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import arrow.core.Either
import com.vincent.jetmp3.data.constants.ArtistType
import com.vincent.jetmp3.data.models.NestArtist
import com.vincent.jetmp3.data.models.SpotifyArtist
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.utils.PlaybackState
import com.vincent.jetmp3.utils.functions.TrackAdditions
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
class MediaServiceHandler @UnstableApi
@Inject constructor(
	private val exoPlayer: ExoPlayer,
	private val trackAdditions: TrackAdditions
) : Player.Listener {

	private val trackCache = mutableMapOf<Long, Track>()
	private val artistCache = mutableMapOf<Long, Either<NestArtist?, SpotifyArtist?>>()

	private val _playerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.Initial)
	val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

	private val _playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(PlaybackState())
	val playbackState = _playbackState.asStateFlow()

	private var job: Job? = null
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

	init {
		exoPlayer.addListener(this)
	}

	fun setMediaItemList(tracks: List<Track>, index: Int = 0) {
		if (_playbackState.value.queue == tracks) return setIndex(index)

		val items = tracks.map { track ->
			trackCache[track.id] = track // Cache track by ID
			track.toMediaItem()
		}

		_playbackState.value = _playbackState.value.copy(
			queue = tracks,
			trackList = items
		)

		exoPlayer.setMediaItems(items, index, 0)
		exoPlayer.prepare()
	}

	fun setIndex(index: Int = 0) {
		if (index == exoPlayer.currentMediaItemIndex) return
		exoPlayer.seekToDefaultPosition(index)
		exoPlayer.playWhenReady = true
		_playerState.value = PlayerState.Playing(true)
	}

	fun switchShuffleMode() {
		val isShuffled = exoPlayer.shuffleModeEnabled
		exoPlayer.shuffleModeEnabled = !isShuffled
		_playbackState.value = _playbackState.value.copy(
			isShuffleMode = !isShuffled
		)
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

	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		super.onMediaItemTransition(mediaItem, reason)
		_playerState.value = PlayerState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
		updatePlaybackState()
		updateCurrentArtist()
		addToHistory(trackId = mediaItem?.mediaId ?: "-1")

	}

	private fun updateCurrentArtist() {
		val track = _playbackState.value.currentMediaItem ?: return
		val trackId = track.mediaId.toLongOrNull() ?: return

		artistCache[trackId]?.let { cached ->
			_playbackState.value = _playbackState.value.copy(currentArtist = cached)
			return
		}

		scope.launch {
			val artist: Either<NestArtist?, SpotifyArtist?> = when (track.mediaMetadata.artist) {
				ArtistType.NestArtist.name -> Either.Left(
					trackAdditions.getNestArtist(
						track.mediaMetadata.extras?.getString("artistId").orEmpty()
					)
				)

				ArtistType.SpotifyArtist.name -> Either.Right(
					trackAdditions.getSpotifyArtist(
						track.mediaMetadata.extras?.getString("artistId").orEmpty()
					)
				)

				else -> Either.Left(null)
			}

			artistCache[trackId] = artist

			_playbackState.value = _playbackState.value.copy(
				currentArtist = artist
			)
		}
	}

	private fun addToHistory(trackId: String) = scope.launch {
		trackAdditions.addListenHistory(trackId)
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
			isBuffering = isBuffering,
			duration = exoPlayer.duration,
			currentIndex = exoPlayer.currentMediaItemIndex,
			currentPosition = exoPlayer.currentPosition,
			currentMediaItem = exoPlayer.currentMediaItem,
			bufferedPosition = exoPlayer.bufferedPosition,
//			trackList = exoPlayer.currentTimeline.windowCount.takeIf { it > 0 }?.let {
//				(0 until it).mapNotNull { i -> exoPlayer.getMediaItemAt(i) }
//			} ?: emptyList(),
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