package com.vincent.jetmp3.domain.models.response

import com.squareup.moshi.Json
import com.vincent.jetmp3.data.models.NestArtist
import com.vincent.jetmp3.data.models.Track

data class SearchResult(
	@Json(name = "artists")
	val artists: List<NestArtist>?,
	@Json(name = "tracks")
	val tracks: List<Track>?,
)