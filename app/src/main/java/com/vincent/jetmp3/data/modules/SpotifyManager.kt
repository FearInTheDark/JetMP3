package com.vincent.jetmp3.data.modules

import android.app.Application
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vincent.jetmp3.BuildConfig
import com.vincent.jetmp3.data.datastore.apiTokenDataStore
import com.vincent.jetmp3.data.enums.FetchState
import com.vincent.jetmp3.domain.SpotifyDeveloperService
import com.vincent.jetmp3.domain.SpotifyDeveloperTokenService
import com.vincent.jetmp3.domain.models.Artist
import com.vincent.jetmp3.domain.models.SpotifyTokenResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyManager @Inject constructor(
	private val context: Application,
	private val spotifyDeveloperTokenService: SpotifyDeveloperTokenService,
	private val spotifyDeveloperService: SpotifyDeveloperService
) {
	private val _token: MutableStateFlow<SpotifyTokenResponse> =
		MutableStateFlow(SpotifyTokenResponse())
	val token: StateFlow<SpotifyTokenResponse>
		get() = _token

	private val _fetchState = MutableStateFlow(FetchState.LOADING)
	val fetchState: StateFlow<FetchState>
		get() = _fetchState

	private val _fetchedArtist = MutableStateFlow<Artist?>(null)
	val fetchedArtist: StateFlow<Artist?>
		get() = _fetchedArtist

	private val artistId = "1Xyo4u8uXC1ZmMpatF05PJ"
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

	init {
		scope.launch {
			restoreApiToken()
		}
	}

	suspend fun fetchArtistInfo(ids: String = artistId) {
		this._fetchState.value = FetchState.LOADING

		while (_token.value.accessToken.isEmpty()) {
			Log.d("SpotifyManager", "fetchArtistInfo: Token is empty")
			delay(1000)
		}

		try {
			val response =
				spotifyDeveloperService.fetchArtist(ids, "Bearer ${_token.value.accessToken}")
			val artist = response.body()

			if (!response.isSuccessful || artist == null) {
				if (response.code() == 401) {
					Log.d("SpotifyManager", "fetchArtistInfo: Token expired")
					getToken()
					return this.fetchArtistInfo()
				}
				_fetchState.value = FetchState.ERROR
				Log.e("SpotifyManager", "fetchArtistInfo: ${response.errorBody()?.string()}")
				return
			}
			_fetchedArtist.value = artist
			Log.d("SpotifyManager", "fetchArtistInfo: ${_fetchedArtist.value}")

			_fetchState.value = FetchState.SUCCESS
		} catch (e: Exception) {
			_fetchState.value = FetchState.ERROR
			Log.e("SpotifyManager", "fetchArtistInfo: ${e.message}")
		}
	}

	private suspend fun getToken() {
		val credentials =
			"${BuildConfig.SPOTIFY_CLIENT_ID}:${BuildConfig.SPOTIFY_CLIENT_SECRET}"
		val authHeader =
			"Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
		val response = spotifyDeveloperTokenService.getToken(authorization = authHeader)

		val token = response.body()
		if (!response.isSuccessful || token == null) {
			_fetchState.value = FetchState.ERROR
			Log.e("SpotifyManager", "getAccessToken: ${response.errorBody()?.string()}")
			return
		}
		persistApiToken(token)
		_token.value = token
		Log.d("SpotifyManager", "getAccessToken: ${_token.value.accessToken}")
		return
	}

	private suspend fun persistApiToken(token: SpotifyTokenResponse) {
		context.apiTokenDataStore.edit { prefs ->
			prefs[stringPreferencesKey("access_token")] = token.accessToken
			prefs[stringPreferencesKey("token_type")] = token.tokenType
			prefs[intPreferencesKey("expires_in")] = token.expiresIn
			prefs[stringPreferencesKey("created_at")] = System.currentTimeMillis().toString()
		}
	}

	private suspend fun restoreApiToken() {
		val prefs = context.apiTokenDataStore.data.first()
		val accessToken = prefs[stringPreferencesKey("access_token")]
		val tokenType = prefs[stringPreferencesKey("token_type")]
		val expiresIn = prefs[intPreferencesKey("expires_in")]
		val createdAt = prefs[stringPreferencesKey("created_at")]
		if (createdAt != null) {
			val currentTime = System.currentTimeMillis()
			val tokenAge = (currentTime - createdAt.toLong()) / 1000
			if (tokenAge < expiresIn!!) {
				_token.value = SpotifyTokenResponse(
					accessToken = accessToken!!,
					tokenType = tokenType!!,
					expiresIn = expiresIn
				)
				Log.d("SpotifyManager", "restoreApiToken: ${_token.value}")
				delay(1000)
				return
			}
		}
		Log.e("SpotifyManager", "restoreApiToken: Token is null")
		getToken()
	}
}


