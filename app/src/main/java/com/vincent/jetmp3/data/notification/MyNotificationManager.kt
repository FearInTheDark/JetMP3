package com.vincent.jetmp3.data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.vincent.jetmp3.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MyNotificationManager @Inject constructor(
	@ApplicationContext private val context: Context,
	private val exoPlayer: ExoPlayer
) {

	private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

	init {
		createNotificationChannel()
	}

	fun startNotificationService(
		mediaSessionService: MediaSessionService,
		mediaSession: MediaSession
	) {
		buildNotification(mediaSession)
		startForegroundNotificationService(mediaSessionService)
	}

	private fun startForegroundNotificationService(mediaSessionService: MediaSessionService) {
		val notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
			.setCategory(Notification.CATEGORY_SERVICE)
			.build()
		mediaSessionService.startForeground(NOTIFICATION_ID, notification)
	}

	@OptIn(UnstableApi::class)
	private fun buildNotification(mediaSession: MediaSession) {
		PlayerNotificationManager.Builder(
			context,
			NOTIFICATION_ID,
			NOTIFICATION_CHANNEL_ID
		).setMediaDescriptionAdapter(
			MyNotificationAdapter(
				context = context,
				pendingIntent = mediaSession.sessionActivity
			)
		)
			.setSmallIconResourceId(R.drawable.logos__google_bard_icon)
			.build()
			.also {
				it.setMediaSessionToken(mediaSession.platformToken)
				it.setUseFastForwardAction(true)
				it.setUseRewindActionInCompactView(true)
				it.setUseNextActionInCompactView(true)
				it.setPriority(NotificationCompat.PRIORITY_LOW)
				it.setPlayer(exoPlayer)
			}
	}

	private fun createNotificationChannel() {
		val channel = NotificationChannel(
			NOTIFICATION_CHANNEL_ID,
			NOTIFICATION_CHANNEL_NAME,
			NotificationManager.IMPORTANCE_LOW
		)
		notificationManager.createNotificationChannel(channel)
	}

	companion object {
		private const val NOTIFICATION_ID = 1
		private const val NOTIFICATION_CHANNEL_NAME = "JetMP3"
		private const val NOTIFICATION_CHANNEL_ID = "jetmp3_notification_channel"
	}
}