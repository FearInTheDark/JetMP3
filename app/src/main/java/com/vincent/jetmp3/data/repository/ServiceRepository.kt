package com.vincent.jetmp3.data.repository

import android.content.Context
import android.content.Intent
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.media.service.MusicPlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(
	@ApplicationContext private val context: Context
) {
	private var isServiceRunning = false

	@UnstableApi
	fun startServiceRunning() {
		if (!isServiceRunning) {
			val intent = Intent(context, MusicPlaybackService::class.java)
			context.startForegroundService(intent)
			isServiceRunning = true
		}
	}

	@UnstableApi
	fun stopServiceRunning() {
		if (isServiceRunning) {
			val intent = Intent(context, MusicPlaybackService::class.java)
			context.stopService(intent)
			isServiceRunning = false
		}
	}

	fun isRunning(): Boolean = isServiceRunning
}