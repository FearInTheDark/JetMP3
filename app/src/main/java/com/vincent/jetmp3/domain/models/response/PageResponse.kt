package com.vincent.jetmp3.domain.models.response

import com.vincent.jetmp3.data.models.Track

data class PageResponse(
	val tracks: List<Track> = emptyList(),
	val total: Int = 0,
)