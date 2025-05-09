package com.vincent.jetmp3.data.repository

import android.util.Log
import com.vincent.jetmp3.data.resolver.AudioResolverHelper
import com.vincent.jetmp3.domain.NestService
import com.vincent.jetmp3.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
	private val audioResolverHelper: AudioResolverHelper,
	private val nestService: NestService,
	private val authRepository: AuthRepository
) {
	suspend fun getLocalTracks(): List<Track> = withContext(Dispatchers.Main) {
		audioResolverHelper.getLocalTracks()
	}

	suspend fun getNestAudioData() = withContext(Dispatchers.Main) {
		while (authRepository.authenticating.value) {
			delay(100)
		}
//		Log.d("AudioRepository", authRepository.accessToken.value)
		val response = nestService.getAllTracks(auth = "Bearer ".plus(authRepository.accessToken.value))
		if (response.isSuccessful) {
			Log.d("AudioRepository", "Response: ${response.body()}")
			val tracks = response.body()
			tracks
		} else {
			Log.d("AudioRepository", "Error: $response")
			null
		}
	}
}