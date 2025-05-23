package com.vincent.jetmp3.data.repository

import android.app.Application
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vincent.jetmp3.BuildConfig
import com.vincent.jetmp3.core.annotation.ApplicationScope
import com.vincent.jetmp3.data.constants.FetchState
import com.vincent.jetmp3.data.datastore.apiTokenDataStore
import com.vincent.jetmp3.data.models.SpotifyArtist
import com.vincent.jetmp3.domain.SpotifyDeveloperService
import com.vincent.jetmp3.domain.SpotifyDeveloperTokenService
import com.vincent.jetmp3.domain.models.SpotifyToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okio.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyRepository @Inject constructor(
	private val context: Application,
	private val spotifyDeveloperTokenService: SpotifyDeveloperTokenService,
	private val spotifyDeveloperService: SpotifyDeveloperService,
	@ApplicationScope
	private val coroutineScope: CoroutineScope
) {
	private val _token: MutableStateFlow<SpotifyToken> = MutableStateFlow(SpotifyToken())

	private val _fetchState = MutableStateFlow(FetchState.LOADING)

	private val _fetchedArtist = MutableStateFlow<SpotifyArtist?>(null)

	private val artistId = "1Xyo4u8uXC1ZmMpatF05PJ"

	init {
		coroutineScope.launch {
			restoreApiToken()
		}
	}

	suspend fun fetchArtistInfo(ids: String = artistId): SpotifyArtist? {
		this._fetchState.value = FetchState.LOADING

		while (_token.value.accessToken.isEmpty()) {
			Log.e("SpotifyManager", "fetchArtistInfo: Token is empty")
			delay(1000)
		}

		try {
			val response = spotifyDeveloperService.fetchArtist(ids, "Bearer ${_token.value.accessToken}")
			val artist = response.body()

			if (!response.isSuccessful || artist == null) {
				if (response.code() == 401) {
					Log.d("SpotifyManager", "fetchArtistInfo: Token expired")
					getToken()
					return this.fetchArtistInfo()
				}
				_fetchState.value = FetchState.ERROR
				Log.e("SpotifyManager", "fetchArtistInfo: ${response.errorBody()?.string()}")
			}
			_fetchedArtist.value = artist
			Log.d("SpotifyManager", "fetchArtistInfo: ${_fetchedArtist.value}")

			_fetchState.value = FetchState.SUCCESS
			return artist
		} catch (io: IOException) {
			_fetchState.value = FetchState.ERROR
			Log.e("SpotifyManager", "fetchArtistInfo: ${io.message}")
			return null
		} catch (e: Exception) {
			_fetchState.value = FetchState.ERROR
			Log.e("SpotifyManager", "fetchArtistInfo: ${e.message}")
			return null
		} catch (e: Throwable) {
			_fetchState.value = FetchState.ERROR
			Log.e("SpotifyManager", "fetchArtistInfo: ${e.message}")
			return null
		} catch (e: SocketTimeoutException) {
			Log.e("TrackRepository", "SocketTimeoutException: ${e.message}")
			return null
		} finally {
			delay(1000)
		}
	}

	private suspend fun getToken() {
		val credentials = "${BuildConfig.SPOTIFY_CLIENT_ID}:${BuildConfig.SPOTIFY_CLIENT_SECRET}"
		val authHeader = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
		val response = spotifyDeveloperTokenService.getToken(authorization = authHeader)
		Log.d("SpotifyManager", "getAccessToken: ${response.body()}")
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

	private suspend fun persistApiToken(token: SpotifyToken) {
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
				_token.value = SpotifyToken(
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


