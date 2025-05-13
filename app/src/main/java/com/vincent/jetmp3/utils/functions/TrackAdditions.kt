package com.vincent.jetmp3.utils.functions

import com.vincent.jetmp3.data.models.NestArtist
import com.vincent.jetmp3.data.models.SpotifyArtist
import com.vincent.jetmp3.data.repository.AuthRepository
import com.vincent.jetmp3.data.repository.SpotifyRepository
import com.vincent.jetmp3.domain.NestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrackAdditions @Inject constructor(
	private val nestService: NestService,
	private val authRepository: AuthRepository,
	private val spotifyRepository: SpotifyRepository
) {

	suspend fun getNestArtist(id: String): NestArtist? = withContext(Dispatchers.IO) {
		val response = nestService.getArtist(
			auth = "Bearer ${authRepository.accessToken.value}",
			artistId = id
		)
		if (response.isSuccessful) {
			response.body()
		} else {
			throw Exception("Failed to fetch artist data")
		}
	}

	suspend fun getSpotifyArtist(id: String): SpotifyArtist? = withContext(Dispatchers.IO) {
		spotifyRepository.fetchArtistInfo(id)
	}
}