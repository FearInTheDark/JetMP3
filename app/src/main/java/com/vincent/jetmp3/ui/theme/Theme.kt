package com.vincent.jetmp3.ui.theme

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
	primary = Blue80,
	secondary = PurpleGrey80,
	tertiary = DarkNowPlayingBar,
	surface = DarkSurface,
	onSurface = DarOnSurface,
	background = DarkBackground,
)

private val LightColorScheme = lightColorScheme(
	primary = Purple40,
	secondary = PurpleGrey40,
	tertiary = LightNowPlayingBar,
	background = LightBackground

	/* Other default colors to override
	background = Color(0xFFFFFBFE),
	surface = Color(0xFFFFFBFE),
	onPrimary = Color.White,
	onSecondary = Color.White,
	onTertiary = Color.White,
	onBackground = Color(0xFF1C1B1F),
	onSurface = Color(0xFF1C1B1F),
	*/
)

@Composable
fun JetMP3Theme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}

	Log.d("Theme", "darkTheme: $darkTheme | ColorScheme: $colorScheme")

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}