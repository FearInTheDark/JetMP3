package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.request.ForgotRequest
import com.vincent.jetmp3.domain.models.request.LoginRequest
import com.vincent.jetmp3.domain.models.request.ResetRequest
import com.vincent.jetmp3.domain.models.request.SignupRequest
import com.vincent.jetmp3.domain.models.response.ForgotResponse
import com.vincent.jetmp3.domain.models.response.LoginResponse
import com.vincent.jetmp3.domain.models.response.NestResponse
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

	@POST("forgot-password")
	suspend fun forgotPassword(
		@Body request: ForgotRequest
	): Response<ForgotResponse>

	@POST("reset-password")
	suspend fun resetPassword(
		@Body resetRequest: ResetRequest
	): Response<NestResponse>
}