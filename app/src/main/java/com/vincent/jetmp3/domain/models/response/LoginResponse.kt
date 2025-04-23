package com.vincent.jetmp3.domain.models.response

import com.squareup.moshi.Json

data class LoginResponse(
	@Json(name = "access_token")
	val tokenResponse: String,
	@Json(name = "expired_in")
	val expiredIn: String
)