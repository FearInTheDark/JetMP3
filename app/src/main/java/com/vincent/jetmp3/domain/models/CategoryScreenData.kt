package com.vincent.jetmp3.domain.models

import com.vincent.jetmp3.domain.models.response.PageResponse

data class CategoryScreenData (
	val title: String,
	val thumbnailUri: String,
	val data: PageResponse
)