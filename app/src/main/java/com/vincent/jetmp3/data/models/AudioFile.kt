package com.vincent.jetmp3.data.models

data class AudioFile(
	val id: Long,
	val title: String,
	val artist: String,
	val uri: String,
	val duration: Long,
	val type: String = "local"
)