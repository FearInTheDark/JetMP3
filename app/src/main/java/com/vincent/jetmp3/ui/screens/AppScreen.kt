package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vincent.jetmp3.ui.components.navigation.MyNavigationBar
import com.vincent.jetmp3.ui.screens.auth.AuthScreen
import com.vincent.jetmp3.ui.viewmodels.MusicViewModel
import com.vincent.jetmp3.utils.Screen

@Composable
fun AppScreen() {
	val navController = rememberNavController()
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute = navBackStackEntry?.destination?.route

	val showNavBar = remember(currentRoute) {
		currentRoute !in listOf(
			Screen.Auth.route,
			Screen.PlayerScreen.route
		)
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.surface),
		contentAlignment = Alignment.Center,
	) {
		AppNavHost(navController)

		if (showNavBar) {

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(80.dp)
					.align(Alignment.BottomCenter)
					.background(
						Brush.verticalGradient(
							colors = if (isSystemInDarkTheme()) listOf(
								Color.Black.copy(0.1f),
								Color.Black.copy(0.3f),
								Color.Black.copy(0.8f),
								Color.Black.copy(0.8f),
								Color.Black.copy(0.85f),
								Color.Black.copy(0.9f),
								Color.Black,
							) else listOf(
								Color.White.copy(0.1f),
								Color.White.copy(0.3f),
								Color.White.copy(0.8f),
								Color.White.copy(0.8f),
								Color.White.copy(0.85f),
								Color.White.copy(0.9f),
								Color.White,
							)
						)
					)

			)

			Column(
				modifier = Modifier
					.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Bottom
			) {
				MyNavigationBar(
					modifier = Modifier
						.fillMaxWidth()
						.background(
							Brush.verticalGradient(
								colors = if (isSystemInDarkTheme()) listOf(
									Color.Transparent,
									Color.Black.copy(0.7f),
								) else  listOf(
									Color.Transparent,
									Color.Gray.copy(0.8f),
								),
								startY = 0f,
								endY = Float.POSITIVE_INFINITY,
							)
						)
						.padding(horizontal = 8.dp),
					navController = navController
				)
			}
		}
	}
}

@Composable
fun AppNavHost(navController: NavHostController) {
	val musicViewModel: MusicViewModel = hiltViewModel<MusicViewModel>()
	NavHost(
		navController = navController,
		startDestination = Screen.Auth.route,
		route = "main_graph"
	) {
		composable(route = Screen.Auth.route) {
			AuthScreen(navController = navController, onLogin = { navController.navigate("home") })
		}
		composable(route = Screen.Home.route) {
			SongListScreen(musicViewModel)
		}

		composable(route = Screen.Search.route) {
			SearchScreen()
		}

		composable(route = Screen.NowPlaying.route) {
			PlayingScreen(musicViewModel)
		}

	}

}