package com.vincent.jetmp3.utils

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.vincent.jetmp3.data.models.AudioFile
import com.vincent.jetmp3.domain.models.PaletteColor
import com.vincent.jetmp3.domain.models.response.TokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Date

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

fun decodeJwt(token: String): TokenResponse? {
	return try {
		val parts = token.split(".")
		if (parts.size != 3) return null

		val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
		val json = JSONObject(payload)

		val userId = json.getInt("userId")
		val email = json.getString("email")
		val issuedAt = Date(json.getLong("iat") * 1000)
		val expiresAt = Date(json.getLong("exp") * 1000)

		TokenResponse(userId, email, issuedAt, expiresAt)
	} catch (e: Exception) {
		e.printStackTrace()
		null
	}
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
		try {
			val imageLoader = ImageLoader(context)
			val request = ImageRequest.Builder(context)
				.data(imageUrl)
				.allowHardware(false) // Important for Palette to work correctly
				.build()

			val result = imageLoader.execute(request)
			val drawable = (result as? SuccessResult)?.drawable
			val bitmap = (drawable as? BitmapDrawable)?.bitmap

			if (bitmap != null) {
				val palette = Palette.from(bitmap).generate()
				val dominant = palette.getDominantColor(defaultColor.toArgb())
				Log.d("TAG", "Dominant color: ${Color(dominant)}")
				Color(dominant)
			} else {
				defaultColor
			}
		} catch (e: Exception) {
			Log.e("TAG", "Error getting dominant color", e)
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

	colors.forEach { (color, weight) ->
		r += color.red * weight
		g += color.green * weight
		b += color.blue * weight
		a += color.alpha * weight
		totalWeight += weight
	}

	if (totalWeight <= 0f) return Color.Transparent
	return Color(r / totalWeight, g / totalWeight, b / totalWeight, a / totalWeight)
}

fun paletteToColor(paletteColor: PaletteColor): Color {
	require(paletteColor.rgb.size == 3) {
		"PaletteColor must have exactly 3 RGB values"
	}

	return Color(paletteColor.rgb[0].toInt(), paletteColor.rgb[1].toInt(), paletteColor.rgb[2].toInt())
}