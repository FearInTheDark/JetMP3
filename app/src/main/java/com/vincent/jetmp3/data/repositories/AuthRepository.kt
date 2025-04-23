package com.vincent.jetmp3.data.repositories

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vincent.jetmp3.data.datastore.authToken
import com.vincent.jetmp3.domain.AuthService
import com.vincent.jetmp3.domain.models.request.LoginRequest
import com.vincent.jetmp3.utils.decodeJwt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
	private val context: Application,
	private val authService: AuthService
) {
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
	private val accessTokenKey = stringPreferencesKey("access_token")
	private val expiredAtKey = stringPreferencesKey("expired_at")

	private val _authenticating : MutableStateFlow<Boolean> = MutableStateFlow(true)
	val authenticating = _authenticating.asStateFlow()

	private val _authValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
	val authValid = _authValid.asStateFlow()

	init {
		scope.launch {
			validateAuthState()
		}
	}

	fun login(request: LoginRequest) {
		scope.launch {
			try {
				val tokenResponse = authService.login(request).body()
				tokenResponse?.let {
					saveToken(it.tokenResponse)
					_authValid.value = true
				}
			}catch (e: Exception) {
				e.printStackTrace()
				_authValid.value = false
			}
		}
	}

	fun logout() {
		scope.launch {
			context.authToken.edit { prefs ->
				prefs[accessTokenKey] = ""
				prefs[expiredAtKey] = ""
			}
			_authValid.value = false
		}
	}

	private suspend fun saveToken(token: String) {
		val decoded = decodeJwt(token)
		Log.d("AuthRepository", "saveToken: $decoded")
		decoded?.let {
			context.authToken.edit { prefs ->
				prefs[accessTokenKey] = token
				prefs[expiredAtKey] = decoded.expiresAt.time.toString()
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

		_authenticating.value = false
	}

}
