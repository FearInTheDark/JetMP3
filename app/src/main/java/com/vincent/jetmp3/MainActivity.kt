package com.vincent.jetmp3

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.vincent.jetmp3.ui.screens.AppScreen
import com.vincent.jetmp3.ui.theme.JetMP3Theme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@ExperimentalMaterial3ExpressiveApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	@ExperimentalMaterial3Api
	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	override fun onCreate(savedInstanceState: Bundle?) {
		installSplashScreen()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			JetMP3Theme(darkTheme = true) {
				AppScreen()
			}
		}
	}
}