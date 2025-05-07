package com.vincent.jetmp3.domain.models.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class LoginResponse(
	@Json(name = "message")
	@SerializedName("message")
	val message: List<String>?,
	@Json(name = "error")
	@SerializedName("error")
	val error: String?,
	@Json(name = "statusCode")
	@SerializedName("statusCode")
	val statusCode: Int?,
	@Json(name = "access_token")
	@SerializedName("access_token")
	val tokenResponse: String?,
	@Json(name = "expired_in")
	@SerializedName("expired_in")
	val expiredIn: String?
)