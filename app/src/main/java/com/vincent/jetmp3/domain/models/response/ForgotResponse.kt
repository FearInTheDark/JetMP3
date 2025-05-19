package com.vincent.jetmp3.domain.models.response

data class ForgotResponse(
	val message: String,
	val token: String,
	val otp: String,
)