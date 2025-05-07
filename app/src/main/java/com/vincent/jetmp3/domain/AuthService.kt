package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.request.LoginRequest
import com.vincent.jetmp3.domain.models.request.SignupRequest
import com.vincent.jetmp3.domain.models.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
	@POST("signup")
	suspend fun register(
		@Body signupRequest: SignupRequest
	): Response<LoginResponse>

	@POST("signin")
	suspend fun login(
		@Body loginRequest: LoginRequest
	): Response<LoginResponse>
}