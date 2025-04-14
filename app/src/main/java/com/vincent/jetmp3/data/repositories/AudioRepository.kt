package com.vincent.jetmp3.data.repositories

import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.data.resolver.AudioResolverHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
	private val audioResolverHelper: AudioResolverHelper
) {
	suspend fun getAudioData(): List<AudioFile> = withContext(Dispatchers.Main) {
		audioResolverHelper.getAudioData()
	}
}