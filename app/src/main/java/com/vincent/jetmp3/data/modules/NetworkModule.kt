package com.vincent.jetmp3.data.modules

import com.vincent.jetmp3.domain.ApiService
import com.vincent.jetmp3.domain.SpotifyDeveloperService
import com.vincent.jetmp3.domain.SpotifyDeveloperTokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
	@Provides
	@Singleton
	fun provideRetrofit(): Builder {
		return Builder()
			.addConverterFactory(GsonConverterFactory.create())
	}

	@Provides
	@Singleton
	fun provideApiService(builder: Builder): ApiService {
		return builder.baseUrl("https://jsonplaceholder.typicode.com/")
			.build()
			.create(ApiService::class.java)
	}

	@Provides
	@Singleton
	fun provideSpotDevTokenService(builder: Builder): SpotifyDeveloperTokenService {
		return builder.baseUrl("https://accounts.spotify.com/api/")
			.build()
			.create(SpotifyDeveloperTokenService::class.java)
	}

	@Provides
	@Singleton
	fun provideSpotDevService(builder: Builder): SpotifyDeveloperService {
		return builder.baseUrl("https://api.spotify.com/v1/")
			.build().create(SpotifyDeveloperService::class.java)
	}
}