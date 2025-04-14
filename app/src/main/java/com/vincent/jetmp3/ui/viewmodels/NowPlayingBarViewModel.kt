package com.vincent.jetmp3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingBarViewModel @Inject constructor(
	private val exoPlayer: ExoPlayer
): ViewModel() {
	val currentSong: MediaItem? = exoPlayer.currentMediaItem
	val isPlaying = exoPlayer.isPlaying
	val progress = exoPlayer.currentPosition

	fun pause() = exoPlayer.pause()

}