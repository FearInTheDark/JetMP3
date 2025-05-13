package com.vincent.jetmp3.data.modules

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vincent.jetmp3.domain.AuthService
import com.vincent.jetmp3.domain.ImagePaletteService
import com.vincent.jetmp3.domain.NestService
import com.vincent.jetmp3.domain.SpotifyDeveloperService
import com.vincent.jetmp3.domain.SpotifyDeveloperTokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun provideMoshi(): Moshi {
		return Moshi.Builder()
			.add(KotlinJsonAdapterFactory()).build()
	}

	@Provides
	@Singleton
	fun provideRetrofit(moshi: Moshi): Builder {
		return Builder()
			.addConverterFactory(MoshiConverterFactory.create(moshi))
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

	@Provides
	@Singleton
	fun provideImagePaletteService(builder: Builder): ImagePaletteService {
		return builder.baseUrl("https://image-palette-extractor.vercel.app/")
			.build().create(ImagePaletteService::class.java)
	}

	@Provides
	@Singleton
	fun provideNestService(builder: Builder): NestService {
		return builder.baseUrl("https://alright-armadillo-fearinthedark-cc1fd128.koyeb.app/api/")
			.build().create(NestService::class.java)
	}

	@Provides
	@Singleton
	fun provideAuthService(builder: Builder): AuthService {
		return builder.baseUrl("https://alright-armadillo-fearinthedark-cc1fd128.koyeb.app/api/auth/")
			.build().create(AuthService::class.java)
	}

}