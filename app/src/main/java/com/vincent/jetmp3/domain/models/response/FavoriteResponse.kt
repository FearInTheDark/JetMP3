package com.vincent.jetmp3.domain.models.response

import com.vincent.jetmp3.data.constants.FavoriteType

data class FavoriteResponse(
	val message: String,
	val success: Boolean,
	val action: FavoriteType
)