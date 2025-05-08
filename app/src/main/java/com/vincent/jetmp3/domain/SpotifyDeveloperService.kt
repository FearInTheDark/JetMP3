package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.SpotifyArtist
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyDeveloperService {
	@GET("artists/{ids}")
	suspend fun fetchArtist(
		@Path("ids") ids: String,
		@Header("Authorization") token: String
	): Response<SpotifyArtist>

	@GET("artists")
	suspend fun fetchArtists(
		@Query("ids") ids: String,
		@Header("Authorization") token: String
	): Response<List<SpotifyArtist>>
}