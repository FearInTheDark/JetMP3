package com.vincent.jetmp3.data.repository

import android.util.Log
import com.vincent.jetmp3.domain.NestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
	private val authRepository: AuthRepository,
	private val nestService: NestService
) {
	suspend fun getNestTracks() = withContext(Dispatchers.IO) {
		while (authRepository.authenticating.value) {
			delay(100)
		}
		try {
			val response = nestService.getAllTracks(auth = "Bearer ".plus(authRepository.accessToken.value))
			if (response.isSuccessful) {
				Log.d("TrackRepository", "Response: ${response.body()}")
				response.body()
			} else {
				Log.d("TrackRepository", "Error: $response")
				emptyList()
			}
		} catch (e: IOException) {
			Log.e("TrackRepository", "Error: ${e.message}")
			emptyList()
		} catch (e: Exception) {
			Log.e("TrackRepository", "Error: ${e.message}")
			emptyList()
		}
	}

}