package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.Track
import retrofit2.Response
import retrofit2.http.GET

interface NestService {
	@GET("track")
	suspend fun getAllTracks(): Response<List<Track>>;
}