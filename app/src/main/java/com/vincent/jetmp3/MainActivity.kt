package com.vincent.jetmp3

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vincent.jetmp3.ui.screens.AppScreen
import com.vincent.jetmp3.ui.screens.MusicApp
import com.vincent.jetmp3.ui.theme.JetMP3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@OptIn(ExperimentalPermissionsApi::class)
	@SuppressLint("InlinedApi")
	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			val permissionState =
				rememberPermissionState(android.Manifest.permission.READ_MEDIA_AUDIO)
			LaunchedEffect(Unit) {
				if (!permissionState.status.isGranted) {
					permissionState.launchPermissionRequest()
				}
			}

			if (permissionState.status.isGranted) {
				JetMP3Theme {
					AppScreen()
				}
			} else {
				Text("Permission required to access audio files.")
			}
		}
	}

}