package com.vincent.jetmp3.domain.models

import com.squareup.moshi.Json

data class SpotifyToken(
	@Json(name = "access_token") val accessToken: String,
	@Json(name = "token_type") val tokenType: String,
	@Json(name = "expires_in") val expiresIn: Int
) {
	constructor() : this("", "", 0)
}
