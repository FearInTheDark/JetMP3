package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.Track
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface NestService {
	@GET("tracks")
	suspend fun getAllTracks(@Header("Authorization") auth: String): Response<List<Track>>;


}