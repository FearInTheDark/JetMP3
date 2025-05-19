package com.vincent.jetmp3.utils.functions

import com.vincent.jetmp3.data.models.NestArtist
import com.vincent.jetmp3.data.models.SpotifyArtist
import com.vincent.jetmp3.data.repository.AuthRepository
import com.vincent.jetmp3.data.repository.SpotifyRepository
import com.vincent.jetmp3.domain.NestService
import com.vincent.jetmp3.domain.models.response.NestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrackAdditions @Inject constructor(
	private val nestService: NestService,
	private val authRepository: AuthRepository,
	private val spotifyRepository: SpotifyRepository
) {

	suspend fun getNestArtist(id: String): NestArtist? = safeApiCall {
		nestService.getArtist(
			auth = "Bearer ${authRepository.accessToken.value}",
			artistId = id
		)
	}

	suspend fun getSpotifyArtist(id: String): SpotifyArtist? = withContext(Dispatchers.IO) {
		spotifyRepository.fetchArtistInfo(id)
	}

	suspend fun addListenHistory(id: String): NestResponse? = safeApiCall {
		nestService.addToHistory(
			auth = "Bearer ${authRepository.accessToken.value}",
			trackId = id.toLong()
		)
	}
}