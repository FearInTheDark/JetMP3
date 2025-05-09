package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.domain.ImagePaletteService
import com.vincent.jetmp3.domain.models.request.VibrantRequest
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import com.vincent.jetmp3.utils.paletteToColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val imagePaletteService: ImagePaletteService
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

	suspend fun getDominantColor(imageUrl: String? = null): Color {
		val selectedAudio = playbackState.value.currentTrack ?: return Color.Gray

		try {
			val rgb = viewModelScope.async {
				delay(300)
				imagePaletteService.getPalette(VibrantRequest(imageUrl ?: selectedAudio.images.first()))
			}.await().muted
			return paletteToColor(rgb)
		} catch (e: Exception) {
			return Color.Gray
		}

	}

}