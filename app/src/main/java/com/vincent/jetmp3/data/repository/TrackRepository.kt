package com.vincent.jetmp3.data.repository

import com.vincent.jetmp3.domain.NestService
import com.vincent.jetmp3.utils.functions.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
	private val authRepository: AuthRepository,
	private val nestService: NestService
) {
	suspend fun getNestTracks() = safeApiCall {
		nestService.getAllTracks(auth = "Bearer ".plus(authRepository.accessToken.value))
	}

	suspend fun getNestTrack(trackId: Long) = safeApiCall {
		nestService.getTrack(
			auth = "Bearer ".plus(authRepository.accessToken.value),
			trackId = trackId
		)
	}

	suspend fun getHistoryTracks() = safeApiCall {
		nestService.getHistoryData(auth = "Bearer ".plus(authRepository.accessToken.value))
	}

	suspend fun getFavoriteTracks() = safeApiCall {
		nestService.getFavoriteData(auth = "Bearer ${authRepository.accessToken.value}")
	}

}