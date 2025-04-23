package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.Palette
import com.vincent.jetmp3.domain.models.request.VibrantRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ImagePaletteService {
	@POST("api/v1/vibrant/")
	suspend fun getPalette(
		@Body vibrantRequest: VibrantRequest
	): Palette
}

