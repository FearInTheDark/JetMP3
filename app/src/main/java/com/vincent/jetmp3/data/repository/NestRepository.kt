package com.vincent.jetmp3.data.repository

import android.util.Log
import com.vincent.jetmp3.domain.NestService
import com.vincent.jetmp3.domain.models.response.FavoriteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NestRepository @Inject constructor(
	private val nestService: NestService,
	private val authRepository: AuthRepository
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

	suspend fun toggleFavorite(trackId: Long): FavoriteResponse = withContext(Dispatchers.IO) {
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
}