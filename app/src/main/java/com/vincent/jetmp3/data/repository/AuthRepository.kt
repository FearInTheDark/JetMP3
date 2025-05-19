package com.vincent.jetmp3.data.repository

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.vincent.jetmp3.core.annotation.ApplicationScope
import com.vincent.jetmp3.data.datastore.authToken
import com.vincent.jetmp3.domain.AuthService
import com.vincent.jetmp3.domain.models.request.ForgotRequest
import com.vincent.jetmp3.domain.models.request.LoginRequest
import com.vincent.jetmp3.domain.models.request.ResetRequest
import com.vincent.jetmp3.domain.models.request.SignupRequest
import com.vincent.jetmp3.domain.models.response.LoginResponse
import com.vincent.jetmp3.utils.decodeJwt
import com.vincent.jetmp3.utils.functions.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
	private val context: Application,
	@ApplicationScope private val coroutineScope: CoroutineScope,
	private val authService: AuthService,
) {
	private val accessTokenKey = stringPreferencesKey("access_token")
	private val expiredAtKey = stringPreferencesKey("expired_at")

	private val _authenticating: MutableStateFlow<Boolean> = MutableStateFlow(true)

	private val _authValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
	val authValid = _authValid.asStateFlow()

	private val _accessToken: MutableStateFlow<String> = MutableStateFlow("")
	val accessToken = _accessToken.asStateFlow()

	private val _errorMessage: MutableStateFlow<MutableList<String>> = MutableStateFlow(mutableListOf())
	val errorMessage = _errorMessage.asStateFlow()

	init {
		coroutineScope.launch {
			validateAuthState()
		}
	}

	suspend fun login(request: LoginRequest) {
		try {
			val response = authService.login(request)

			if (response.isSuccessful) {
				response.body()?.tokenResponse?.let {
					saveToken(it)
					_authValid.value = true
				}
			} else {
				val errorBody = response.errorBody()?.string()
				Log.d("AuthRepository", "Error Body: $errorBody")
				errorBody?.let { it ->
					Log.d("AuthRepository", "Error Body: $it")
					val gson = Gson()
					val errorResponse: LoginResponse = gson.fromJson(errorBody, LoginResponse::class.java)

					errorResponse.message?.let { _errorMessage.value = it.toMutableList() }
				}
				_authValid.value = false
			}

		} catch (e: Exception) {
			e.printStackTrace()
			_authValid.value = false
			_errorMessage.value = mutableListOf("Credentials not match, try again!")
		}
	}

	suspend fun register(signupRequest: SignupRequest) {
		try {
			val response = authService.register(signupRequest)

			if (response.isSuccessful) {
				response.body()?.tokenResponse?.let {
					saveToken(it)
					_authValid.value = true
				}
			} else {
				val errorBody = response.errorBody()?.string()
				Log.d("AuthRepository", "Error Body: $errorBody")
				errorBody?.let { it ->
					Log.d("AuthRepository", "Error Body: $it")
					val gson = Gson()
					val errorResponse: LoginResponse = gson.fromJson(errorBody, LoginResponse::class.java)

					errorResponse.message?.let { _errorMessage.value = it.toMutableList() }
				}
				_authValid.value = false
			}

		} catch (e: Exception) {
			e.printStackTrace()
			_authValid.value = false
			_errorMessage.value = mutableListOf("Error occurred, try again!")
		}
	}

	suspend fun forgotPassword(email: String): Boolean {
		val forgotResponse = safeApiCall {
			authService.forgotPassword(ForgotRequest(email))
		}
		if (forgotResponse == null) return false
		_errorMessage.value = mutableListOf(forgotResponse.message)
		val token = forgotResponse.token
		val otp = forgotResponse.otp

		context.authToken.edit { prefs ->
			Log.d("AuthRepository", "forgotPassword: $otp - $token")
			prefs[stringPreferencesKey("forgot_token")] = token
			prefs[stringPreferencesKey("otp")] = otp
		}

		return true
	}

	suspend fun validateOtp(otp: String): Boolean {
		delay(1000)
		val prefs = context.authToken.data.first()
		val savedOtp = prefs[stringPreferencesKey("otp")]
		if (savedOtp != otp) {
			_errorMessage.value = mutableListOf("Invalid OTP")
			return false
		}
		return true
	}

	suspend fun resetPassword(email: String, otp: String, newPassword: String): Boolean {
		val resetResponse = safeApiCall {
			val token = context.authToken.data.first()[stringPreferencesKey("forgot_token")]
			require(token != null) { "Token is null" }
			authService.resetPassword(
				ResetRequest(
					email = email,
					otp = otp,
					token = token,
					newPassword = newPassword
				)
			)
		}

		if (resetResponse == null) return false
		_errorMessage.value = mutableListOf(resetResponse.message)
		return true
	}

	suspend fun logout() {
		context.authToken.edit { prefs ->
			prefs[accessTokenKey] = ""
			prefs[expiredAtKey] = ""
		}
		_authValid.value = false
	}

	private suspend fun saveToken(token: String) {
		val decoded = decodeJwt(token)
		Log.d("AuthRepository", "saveToken: $decoded")
		decoded?.let {
			context.authToken.edit { prefs ->
				prefs[accessTokenKey] = token
				prefs[expiredAtKey] = decoded.expiresAt.time.toString()
				_accessToken.value = token
			}
		}
	}

	private suspend fun validateAuthState() {
		_authenticating.value = true

		val prefs = context.authToken.data.first()
		val token = prefs[accessTokenKey]
		val expiredAt = prefs[expiredAtKey]?.toLongOrNull()

		Log.d("AuthRepository", "validateAuthState: token: $token, expiredAt: $expiredAt")

		val now = System.currentTimeMillis()
		_authValid.value = !(token == null || expiredAt == null || now > expiredAt)

		if (_authValid.value && token != null) {
			_accessToken.value = token
		}

		_authenticating.value = false
	}

	fun clearErrors() {
		_errorMessage.value = mutableListOf()
	}

}
