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