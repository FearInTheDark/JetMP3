package com.vincent.jetmp3.domain.models.request

data class ResetRequest(
	val email: String,
	val otp: String,
	val token: String,
	val newPassword: String
)