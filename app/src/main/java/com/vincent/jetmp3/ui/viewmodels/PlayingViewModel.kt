package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.data.constants.ResponseAction
import com.vincent.jetmp3.data.repository.NestRepository
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val nestRepository: NestRepository,
) : ViewModel() {
	val playbackState = mediaServiceHandler.playbackState

	init {
		// Init log
		Log.d("Initialization", "PlayingViewModel initialized")
	}

	fun playOrPause() = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.PlayPause) }

	fun forward() = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.Forward) }

	fun backward() = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.Backward) }

	fun seekToNext() = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext) }

	fun seekToPrevious() = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrevious) }

	fun updateProgress(newProgress: Float) = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.UpdateProgress(newProgress)) }

	suspend fun toggleFavorite(trackId: Long = playbackState.value.currentTrack?.id ?: 0L): ResponseAction {
		val res = viewModelScope.async {
			nestRepository.toggleFavorite(trackId)
		}.await()
		mediaServiceHandler.updatePlaybackState {
			copy(
				queue = queue.map {
					if (it.id == trackId) {
						it.copy(isFavorite = res!!.action == ResponseAction.ADDED)
					} else it
				},
				currentTrack = currentTrack?.copy(
					isFavorite = res!!.action == ResponseAction.ADDED
				)
			)
		}
		return res!!.action
	}

	suspend fun getDominantColor(imageUrl: String = playbackState.value.currentTrack?.images?.firstOrNull() ?: ""): Color =
		nestRepository.getDominantColor(imageUrl)

}