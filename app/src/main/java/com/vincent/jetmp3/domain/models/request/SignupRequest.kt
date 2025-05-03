package com.vincent.jetmp3.domain.models.request

data class SignupRequest(
	val name: String,
	val email: String,
	val password: String
)