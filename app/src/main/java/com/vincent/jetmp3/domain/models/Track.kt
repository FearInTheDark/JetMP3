package com.vincent.jetmp3.domain.models

import com.squareup.moshi.Json
import com.vincent.jetmp3.data.constants.ArtistType

data class Track(
	@Json(name = "id") val id: Int,
	@Json(name = "name") val name: String,
	@Json(name = "uri") val uri: String,
	@Json(name = "artist_id") val artistId: Int?,
	@Json(name = "artist_type") val artistType: ArtistType?,
	@Json(name = "images") val images: List<String> = emptyList()
)