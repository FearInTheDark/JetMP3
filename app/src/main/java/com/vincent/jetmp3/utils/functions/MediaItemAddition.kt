package com.vincent.jetmp3.utils.functions

import androidx.media3.common.MediaItem

fun MediaItem.string(): String {
	return "MediaItem(" +
			// Show all metadata
			"duration=${mediaMetadata.durationMs}" +
			"title=${mediaMetadata.title}" +
			"artist=${mediaMetadata.artist}" +
			"album=${mediaMetadata.albumTitle}" +
			"artworkUri=${mediaMetadata.artworkUri}" +
			"mediaId=$mediaId"
}

fun durationToString(durationMs: Long): String {
	val totalSeconds = durationMs / 1000
	val minutes = totalSeconds / 60
	val seconds = totalSeconds % 60
	return "%02d:%02d".format(minutes, seconds)
}
