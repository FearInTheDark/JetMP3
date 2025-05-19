package com.vincent.jetmp3.utils

import android.util.Base64
import androidx.compose.ui.graphics.Color
import com.vincent.jetmp3.domain.models.PaletteColor
import com.vincent.jetmp3.domain.models.response.TokenResponse
import org.json.JSONObject
import java.util.Date

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

fun mixColors(colors: Array<Pair<Color, Float>>): Color {
	if (colors.isEmpty()) return Color.Transparent
	var r = 0f
	var g = 0f
	var b = 0f
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

fun getOppositeColor(color: Color): Color {
	return Color(
		red = 1f - color.red,
		green = 1f - color.green,
		blue = 1f - color.blue,
		alpha = color.alpha
	)
}

fun paletteToColor(paletteColor: PaletteColor): Color {
	require(paletteColor.rgb.size == 3) {
		"PaletteColor must have exactly 3 RGB values"
	}

	return Color(paletteColor.rgb[0].toInt(), paletteColor.rgb[1].toInt(), paletteColor.rgb[2].toInt())
}