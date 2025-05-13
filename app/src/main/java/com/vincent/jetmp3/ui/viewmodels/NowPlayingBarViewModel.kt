package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.data.constants.FavoriteType
import com.vincent.jetmp3.data.repository.NestRepository
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
class NowPlayingBarViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,
	private val imagePaletteService: ImagePaletteService,
	private val nestRepository: NestRepository
) : ViewModel() {
	val playbackState = mediaServiceHandler.playbackState

	init {
		// Init log
		Log.d("Initialization", "NowPlayingBarViewModel initialized")
	}

	fun playPause() = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.PlayPause) }

	fun forward() = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext) }

	fun backward() = viewModelScope.launch { mediaServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrevious) }

	suspend fun toggleFavorite(trackId: Long = playbackState.value.currentTrack?.id ?: 0L): FavoriteType {
		val res = viewModelScope.async {
			nestRepository.toggleFavorite(trackId)
		}.await()
		mediaServiceHandler.updatePlaybackState {
			copy(
				currentTrack = currentTrack?.copy(
					isFavorite = res.action == FavoriteType.ADDED
				)
			)
		}
		return res.action
	}

	suspend fun getDominantColor(imageUrl: String? = null): Color {
		val selectedAudio = playbackState.value.currentTrack ?: return Color.Gray

		try {
			val rgb = viewModelScope.async {
				delay(300)
				imagePaletteService.getPalette(VibrantRequest(imageUrl ?: selectedAudio.images.first()))
			}.await().darkVibrant
			Log.d("TAG", "getDominantColor: $rgb")
			return paletteToColor(rgb)
		} catch (e: Exception) {
			Log.e("TAG", "getDominantColor: ${e.message}")
			return Color.Gray
		}

	}

}