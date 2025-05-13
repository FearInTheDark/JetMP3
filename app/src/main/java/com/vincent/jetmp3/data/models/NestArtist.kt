package com.vincent.jetmp3.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class NestArtist(
	@Json(name = "id")	val id: Int,
	@Json(name = "artistId")	val artistId: String,
	@Json(name = "name")	val name: String,
	@Json(name = "uri")	val uri : String?,
	@Json(name = "popularity")	val popularity: Int?,
	@Json(name = "images")	val images: List<String> = listOf(""),
) : Parcelable