package com.vincent.jetmp3.domain.models.response

data class ResponseError(
	val error: ErrorBody
) {
	data class ErrorBody(
		val status: Int,
		val message: String,
	)
}