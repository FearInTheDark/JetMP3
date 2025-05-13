package com.vincent.jetmp3.data.modules

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.vincent.jetmp3.data.notification.MyNotificationManager
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.utils.functions.TrackAdditions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

	@Provides
	@Singleton
	fun provideAudioAttributes(): AudioAttributes =
		AudioAttributes.Builder().setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).setUsage(C.USAGE_MEDIA).build()

	@Provides
	@Singleton
	@UnstableApi
	fun provideExoPlayer(
		@ApplicationContext context: Context,
		attributes: AudioAttributes
	): ExoPlayer = ExoPlayer.Builder(context)
		.setAudioAttributes(attributes, true)
		.setHandleAudioBecomingNoisy(true)
		.setTrackSelector(DefaultTrackSelector(context)).build()

	@Provides
	@Singleton
	fun provideMediaSession(
		@ApplicationContext context: Context,
		player: ExoPlayer,
	): MediaSession = MediaSession.Builder(context, player).build()

	@Provides
	@Singleton
	fun provideNotificationManager(
		@ApplicationContext context: Context, player: ExoPlayer
	): MyNotificationManager = MyNotificationManager(
		context = context, exoPlayer = player
	)

	@Provides
	@Singleton
	fun provideServiceHandler(exoPlayer: ExoPlayer, trackAdditions: TrackAdditions): MediaServiceHandler = MediaServiceHandler(exoPlayer, trackAdditions)
}