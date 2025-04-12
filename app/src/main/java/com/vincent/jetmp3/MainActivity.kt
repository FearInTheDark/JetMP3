package com.vincent.jetmp3

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vincent.jetmp3.ui.screens.AppScreen
import com.vincent.jetmp3.ui.theme.JetMP3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	override fun onCreate(savedInstanceState: Bundle?) {
		installSplashScreen()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			JetMP3Theme {
				AppScreen()
			}
		}
	}

}