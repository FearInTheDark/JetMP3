package com.vincent.jetmp3.domain.models

import com.squareup.moshi.Json

data class Track(
	@Json(name = "id") val id: Int,
	@Json(name = "name") val name: String,
	@Json(name = "uri") val uri: String,
	@Json(name = "duration_ms") val durationMs: Long?,
	@Json(name = "artist_id") val artistId: Int?
)