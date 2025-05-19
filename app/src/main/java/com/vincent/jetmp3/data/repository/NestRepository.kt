package com.vincent.jetmp3.data.repository

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.vincent.jetmp3.domain.ImagePaletteService
import com.vincent.jetmp3.domain.NestService
import com.vincent.jetmp3.domain.models.CategoryScreenData
import com.vincent.jetmp3.domain.models.RecentCategoryItem
import com.vincent.jetmp3.domain.models.request.VibrantRequest
import com.vincent.jetmp3.domain.models.response.NestResponse
import com.vincent.jetmp3.domain.models.response.SearchResult
import com.vincent.jetmp3.utils.functions.safeApiCall
import com.vincent.jetmp3.utils.paletteToColor
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NestRepository @Inject constructor(
	private val nestService: NestService,
	private val authRepository: AuthRepository,
	private val imagePaletteService: ImagePaletteService,
) {
	suspend fun search(query: String): SearchResult? = safeApiCall {
		nestService.search(
			auth = "Bearer ${authRepository.accessToken.value}",
			query = query
		)
	}

	suspend fun toggleFavorite(trackId: Long): NestResponse? = safeApiCall {
		nestService.toggleFavorite(
			auth = "Bearer ${authRepository.accessToken.value}",
			trackId = trackId
		)
	}

	suspend fun addTrackToNewPlaylist(playlistName: String, trackId: Long): NestResponse? = safeApiCall {
		nestService.addTrackToNewPlaylist(
			auth = "Bearer ${authRepository.accessToken.value}",
			trackId = trackId,
			body = mapOf("name" to playlistName)
		)
	}

	suspend fun toggleTrackToPlaylist(playlistId: Number, trackId: Number): NestResponse? = safeApiCall {
		nestService.toggleTrackToPlaylist(
			auth = "Bearer ${authRepository.accessToken.value}",
			playlistId = playlistId,
			trackId = trackId
		)
	}

	suspend fun getDominantColor(imageUrl: String?): Color {
		val defaultColor = Color(0xFF7062f0)

		if (imageUrl == null) return defaultColor

		return try {
			delay(300)
			val rgb = imagePaletteService
				.getPalette(VibrantRequest(imageUrl))
				.darkVibrant

			paletteToColor(rgb)
		} catch (e: IOException) {
			Log.e("Palette", "Network error: ${e.message}")
			defaultColor
		} catch (e: Exception) {
			Log.e("Palette", "Unexpected error: ${e.message}")
			defaultColor
		}
	}

	suspend fun getRecentCategories(): RecentCategoryItem? = safeApiCall {
		nestService.getCategories(
			auth = "Bearer ${authRepository.accessToken.value}"
		)
	}

	suspend fun getCategoryData(path: String): CategoryScreenData? = safeApiCall {
		val authHeader = "Bearer ${authRepository.accessToken.value}"
		val category = path.substringBefore("/")
		val id = path.substringAfterLast("/")

		Log.d("NestRepository", "Path: $path")

		when (category) {
			"favorites" -> nestService.getFavoriteData(authHeader)
			"playlists" -> nestService.getPlaylistData(auth = authHeader, playlistId = id)
			else -> nestService.getHistoryData(authHeader)
		}
	}

	suspend fun deletePlaylist(playlistId: Int): NestResponse? = safeApiCall {
		nestService.deletePlaylist(
			auth = "Bearer ${authRepository.accessToken.value}",
			playlistId = playlistId
		)
	}

}