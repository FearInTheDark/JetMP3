package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.Artist
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SpotifyDeveloperService {
	@GET("artists/{ids}")
	suspend fun fetchArtist(
		@Path("ids") ids: String,
		@Header("Authorization") token: String
	): Response<Artist>
}