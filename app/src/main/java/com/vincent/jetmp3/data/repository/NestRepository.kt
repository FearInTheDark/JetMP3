package com.vincent.jetmp3.data.repository

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.vincent.jetmp3.domain.ImagePaletteService
import com.vincent.jetmp3.domain.NestService
import com.vincent.jetmp3.domain.models.RecentCategoryItem
import com.vincent.jetmp3.domain.models.request.VibrantRequest
import com.vincent.jetmp3.domain.models.response.NestResponse
import com.vincent.jetmp3.utils.paletteToColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NestRepository @Inject constructor(
	private val nestService: NestService,
	private val authRepository: AuthRepository,
	private val imagePaletteService: ImagePaletteService,
) {
	suspend fun search(query: String) = withContext(Dispatchers.IO) {
		try {
			val response = nestService.search(
				auth = "Bearer ${authRepository.accessToken.value}",
				query = query,
			)
			if (response.isSuccessful) {
				Log.d("NestRepository", "Search successful: ${response.body()}")
				response.body()
			} else {
				throw IOException("Error: ${response.errorBody()?.string()}")
			}
		} catch (e: IOException) {
			throw IOException("Network error: ${e.message}")
		} catch (e: Exception) {
			throw Exception("Unexpected error: ${e.message}")
		}
	}

	suspend fun toggleFavorite(trackId: Long): NestResponse = withContext(Dispatchers.IO) {
		try {
			val res = nestService.toggleFavorite(
				auth = "Bearer ${authRepository.accessToken.value}",
				trackId = trackId
			)
			Log.d("NestRepository", "Toggle favorite successful: $res")
			res
		} catch (e: IOException) {
			throw IOException("Network error: ${e.message}")
		} catch (e: Exception) {
			throw Exception("Unexpected error: ${e.message}")
		}
	}

	suspend fun getDominantColor(imageUrl: String): Color {
		return try {
			delay(300)
			val rgb = imagePaletteService.getPalette(VibrantRequest(imageUrl)).darkVibrant
			paletteToColor(rgb)
		} catch (e: Exception) {
			Color.Gray
		} catch (e: IOException) {
			Color.Gray
		}
	}

	suspend fun getRecentCategories(): RecentCategoryItem? = withContext(Dispatchers.IO) {
		try {
			val res = nestService.getCategories(
				auth = "Bearer ${authRepository.accessToken.value}"
			)
			Log.d("NestRepository", "Get recent categories successful: ${res.body()}")
			res.body()
		} catch (e: IOException) {
			Log.e("NestRepository", "Network error: ${e.message}")
			null
		} catch (e: Exception) {
			Log.e("NestRepository", "Unexpected error: ${e.message}")
			null
		}
	}
}