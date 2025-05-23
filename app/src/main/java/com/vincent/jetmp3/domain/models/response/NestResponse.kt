package com.vincent.jetmp3.domain.models.response

import com.vincent.jetmp3.data.constants.ResponseAction

data class NestResponse(
	val message: String,
	val action: ResponseAction
)