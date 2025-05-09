package com.vincent.jetmp3

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.data.repository.AuthRepository
import com.vincent.jetmp3.media.service.MusicPlaybackService
import com.vincent.jetmp3.ui.screens.AppScreen
import com.vincent.jetmp3.ui.theme.JetMP3Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	@Inject
	lateinit var authRepository: AuthRepository
	private var isServiceRunning = false

	@UnstableApi
	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			JetMP3Theme {
				AppScreen {
					startService()
				}
			}
		}
	}

	@UnstableApi
	private fun startService() {
		if (!isServiceRunning) {
			val intent = Intent(this, MusicPlaybackService::class.java)
			startForegroundService(intent)
			isServiceRunning = true
		}
	}
}