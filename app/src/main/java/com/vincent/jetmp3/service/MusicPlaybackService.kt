package com.vincent.jetmp3.service

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.vincent.jetmp3.MainActivity
import com.vincent.jetmp3.R

@UnstableApi
class MusicPlaybackService : MediaSessionService() {
	private var mediaSession: MediaSession? = null
	private lateinit var player: ExoPlayer
	private lateinit var notificationManager: PlayerNotificationManager

	override fun onCreate() {
		super.onCreate()
		player = ExoPlayer.Builder(this).build()

		mediaSession = MediaSession.Builder(this, player).build()

		// Setup notification
		val notificationListener = object : PlayerNotificationManager.NotificationListener {
			override fun onNotificationPosted(notificationId: Int, notification: android.app.Notification, ongoing: Boolean) {
				startForeground(notificationId, notification)
			}

			override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
				stopSelf()
			}
		}

		notificationManager = PlayerNotificationManager.Builder(
			this,
			NOTIFICATION_ID,
			CHANNEL_ID
		)
			.setChannelNameResourceId(R.string.notification_channel_name)
			.setNotificationListener(notificationListener)
			.build()

		notificationManager.setPlayer(player)
		notificationManager.setMediaSessionToken(mediaSession!!.platformToken)

		// Intent to open app when notification is clicked
		val intent = Intent(this, MainActivity::class.java)
		notificationManager.setSmallIcon(R.drawable.logos__spotify_icon)
	}

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
		return mediaSession
	}

	override fun onDestroy() {
		mediaSession?.run {
			player.release()
			release()
			notificationManager.setPlayer(null)
			mediaSession = null
		}
		super.onDestroy()
	}

	companion object {
		private const val NOTIFICATION_ID = 1
		private const val CHANNEL_ID = "music_playback_channel"
	}
}