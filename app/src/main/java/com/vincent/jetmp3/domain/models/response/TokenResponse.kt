package com.vincent.jetmp3.domain.models.response

import java.util.Date

data class TokenResponse(
	val userId: Int,
	val email: String,
	val issuedAt: Date,
	val expiresAt: Date
)