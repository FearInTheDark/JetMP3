package com.vincent.jetmp3.domain.models

import com.squareup.moshi.Json

data class Palette(
	@Json(name = "Vibrant") val vibrant: PaletteColor,
	@Json(name = "DarkVibrant") val darkVibrant: PaletteColor,
	@Json(name = "LightVibrant") val lightVibrant: PaletteColor,
	@Json(name = "Muted") val muted: PaletteColor,
	@Json(name = "DarkMuted") val darkMuted: PaletteColor,
	@Json(name = "LightMuted") val lightMuted: PaletteColor
)
