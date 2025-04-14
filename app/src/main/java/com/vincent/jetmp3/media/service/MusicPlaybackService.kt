package com.vincent.jetmp3.media.service

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.vincent.jetmp3.data.notification.MyNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MusicPlaybackService : MediaSessionService() {
	@Inject
	lateinit var mediaSession: MediaSession

	@Inject
	lateinit var notificationManager: MyNotificationManager

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		notificationManager.startNotificationService(
			mediaSession = mediaSession,
			mediaSessionService = this
		)
		return super.onStartCommand(intent, flags, startId)
	}

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

	override fun onDestroy() {
		super.onDestroy()
		mediaSession.apply {
			release()
			if (player.playbackState != Player.STATE_IDLE) {
				player.seekTo(0)
				player.playWhenReady = false
				player.stop()
			}
		}
	}

}