package com.vincent.jetmp3.data.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
	val id: Long,
	val uri: Uri,
	val data: String,
	val displayName: String,
	val title: String,
	val artist: String,
	val duration: Long,
	val imageSource: String = "https://picsum.photos/500/500",
	val type: String = "local",
) : Parcelable