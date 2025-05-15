package com.vincent.jetmp3.domain.models

data class RecentCategoryItem(
	val favorite: Category,
	val playlists: List<Category> = emptyList(),
	val history: Category
)