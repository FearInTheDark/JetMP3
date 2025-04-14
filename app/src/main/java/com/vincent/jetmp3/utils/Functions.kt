package com.vincent.jetmp3.utils

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.vincent.jetmp3.data.models.AudioFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun AudioFile.toMediaItem(): MediaItem {
	return MediaItem.Builder()
		.setUri(this.uri)
		.setMediaId(this.id.toString())
		.setMediaMetadata(
			MediaMetadata.Builder()
				.setTitle(this.title)
				.setArtist(this.artist)
				.setExtras(Bundle().apply {
					putString("data", this@toMediaItem.data)
					putString("type", this@toMediaItem.type)
				})
				.build()
		)
		.build()
}

suspend fun getDominantColorFromResource(
	context: Context,
	resourceId: Int,
	defaultColor: Color = Color.Gray
): Color {
	return withContext(Dispatchers.Default) {
		val resource = ContextCompat.getDrawable(context, resourceId)
		val bitmap = resource?.toBitmap() ?: return@withContext defaultColor
		val palette = Palette.from(bitmap).generate()
		val dominant = palette.getDominantColor(defaultColor.toArgb())
		Color(dominant)
	}
}

suspend fun getDominantColorFromUrl(
	context: Context,
	imageUrl: String,
	defaultColor: Color = Color.Gray
): Color {
	return withContext(Dispatchers.IO) {
		val imageLoader = ImageLoader(context)
		val request = ImageRequest.Builder(context)
			.data(imageUrl)
			.allowHardware(false)
			.build()

		val result = imageLoader.execute(request)
		val drawable = (result as? SuccessResult)?.drawable

		val bitmap = (drawable as? BitmapDrawable)?.bitmap

		if (bitmap != null) {
			val palette = Palette.from(bitmap).generate()
			val dominant = palette.getDominantColor(defaultColor.toArgb())
			Color(dominant)
		} else {
			defaultColor
		}
	}
}

fun mixColors(colors: Array<Pair<Color, Float>>): Color {
	if (colors.isEmpty()) return Color.Transparent
	var r = 0f;
	var g = 0f;
	var b = 0f;
	var a = 0f
	var totalWeight = 0f

	colors.forEach { (color, weight ) ->
		r += color.red * weight
		g += color.green * weight
		b += color.blue * weight
		a += color.alpha * weight
		totalWeight += weight
	}

	if (totalWeight <= 0f) return Color.Transparent
	return Color(r / totalWeight, g / totalWeight, b / totalWeight, a / totalWeight)
}

fun Modifier.scaleOnTap(
	scale: Float,
	onPressStart: () -> Unit,
	onPressEnd: () -> Unit,
	onTap: (() -> Unit)?
) = this
	.scale(scale)
	.pointerInput(Unit) {
		detectTapGestures(
			onPress = {
				onPressStart()
				try {
					awaitRelease()
				} finally {
					onPressEnd()
				}
			},
			onTap = { onTap?.let { onTap() } }
		)
	}

