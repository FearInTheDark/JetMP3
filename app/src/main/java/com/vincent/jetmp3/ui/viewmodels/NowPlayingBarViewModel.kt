package com.vincent.jetmp3.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.vincent.jetmp3.data.modules.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingBarViewModel @Inject constructor(
	private val playbackManager: PlaybackManager
): ViewModel() {
	val currentSong = playbackManager.currentSong
	val isPlaying = playbackManager.isPlaying
	val progress = playbackManager.progress

	fun pause() = playbackManager.pause()

}