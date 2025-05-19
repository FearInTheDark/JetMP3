package com.vincent.jetmp3.domain.models

import com.vincent.jetmp3.data.constants.CategoryType
import com.vincent.jetmp3.domain.models.response.PageResponse

data class CategoryScreenData (
	val id: Int = 0,
	val title: String,
	val description: String?,
	val thumbnailUri: String?,
	val type: CategoryType,
	val data: PageResponse
)