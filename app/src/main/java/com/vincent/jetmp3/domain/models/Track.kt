package com.vincent.jetmp3.domain.models

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.squareup.moshi.Json
import com.vincent.jetmp3.data.constants.ArtistType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
	@Json(name = "id") val id: Long,
	@Json(name = "name") val name: String,
	@Json(name = "uri") val uri: String,
	@Json(name = "artist_id") val artistId: String?,
	@Json(name = "artist_type") val artistType: ArtistType = ArtistType.NestArtist,
	@Json(name = "images") val images: List<String> = listOf("https://res.cloudinary.com/dsy29z79v/image/upload/v1746724872/music_ztrfid.jpg"),
	@Json(name = "created_at") val createdAt: String?,
) : Parcelable {
	fun toMediaItem() : MediaItem {
		return MediaItem.Builder().setUri(uri)
			.setMediaId(id.toString())
			.setMediaMetadata(
				MediaMetadata.Builder()
					.setTitle(name)
					.setArtist(artistType.name)
					.setArtworkUri(Uri.parse(images.firstOrNull()))
					.setDisplayTitle(name)
					.setExtras(Bundle().apply {
						putString("type", artistType.name)
					})
					.build()
			)
			.build()
	}
}