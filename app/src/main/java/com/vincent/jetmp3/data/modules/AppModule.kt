package com.vincent.jetmp3.data.modules

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.vincent.jetmp3.data.repositories.AudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	@Provides
	@Singleton
	fun provideMediaPlayer(@ApplicationContext context: Context): ExoPlayer =
		ExoPlayer.Builder(context).build()

	@Provides
	@Singleton
	fun provideAudioRepository(): AudioRepository {
		return AudioRepository()
	}

}
