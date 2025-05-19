package com.vincent.jetmp3.domain.models

data class Category (
	val id: Int = 0,
	val title: String,
	val iconUri: String?,
	val url: String,
	val description: String?,
	var trackIds: MutableList<Long> = mutableListOf()
)