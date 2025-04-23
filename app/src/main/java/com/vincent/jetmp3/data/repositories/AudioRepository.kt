package com.vincent.jetmp3.data.repositories

import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.data.resolver.AudioResolverHelper
import com.vincent.jetmp3.domain.NestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
	private val audioResolverHelper: AudioResolverHelper,
	private val nestService: NestService
) {
	suspend fun getLocalAudioData(): List<AudioFile> = withContext(Dispatchers.Main) {
		audioResolverHelper.getAudioData()
	}

	suspend fun getNestAudioData() = withContext(Dispatchers.Main) {
		nestService.getAllTracks().body()
	}
}