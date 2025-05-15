package com.vincent.jetmp3.domain.models.response

import com.vincent.jetmp3.data.models.Track

data class PageResponse(
	val tracks: List<Track>,
	val total: Int,
	val page: Int,
	val size: Int,
)