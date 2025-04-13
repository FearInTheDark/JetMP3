package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.SpotifyTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface SpotifyDeveloperTokenService {
	@FormUrlEncoded
	@POST("token")
	suspend fun getToken(
		@Header("Authorization") authorization: String,
		@Field("grant_type") grantType: String = "client_credentials"
	): Response<SpotifyTokenResponse>
}
