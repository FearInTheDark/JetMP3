package com.vincent.jetmp3.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vincent.jetmp3.ui.components.navigation.MyNavigationBar
import com.vincent.jetmp3.ui.components.navigation.NowPlayingBar
import com.vincent.jetmp3.ui.screens.auth.AuthScreen
import com.vincent.jetmp3.utils.Screen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun AppScreen() {
	val navController = rememberNavController()
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute = navBackStackEntry?.destination?.route
	val permissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_AUDIO)
	val showNavBar by derivedStateOf {
		currentRoute !in listOf(Screen.Auth.route, Screen.NowPlaying.route)
	}

	LaunchedEffect(Unit) {
		if (!permissionState.status.isGranted) {
			permissionState.launchPermissionRequest()
		}
	}

	if (permissionState.status.isGranted) {
		LaunchedEffect(Unit) {
			permissionState.launchPermissionRequest()
		}
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
				NowPlayingBar { navController.navigate(Screen.NowPlaying.route) }
				MyNavigationBar(
					modifier = Modifier
						.fillMaxWidth()
						.background(
							Brush.verticalGradient(
								colors = if (isSystemInDarkTheme()) listOf(
									Color.Transparent,
									Color.Black.copy(0.7f),
								) else listOf(
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
	NavHost(
		navController = navController,
		startDestination = Screen.Auth.route,
		route = "main_graph"
	) {
		composable(
			route = Screen.Auth.route,
			enterTransition = {
				slideIntoContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Left,
					animationSpec = tween(
						durationMillis = 1000,
						delayMillis = 10
					)
				)
			},
			exitTransition = {
				slideOutOfContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Down,
					animationSpec = tween(
						durationMillis = 1000,
						delayMillis = 10
					)
				)
			}
		) {
			AuthScreen(navController = navController, onLogin = { navController.navigate("home") })
		}
		composable(
			route = Screen.Home.route,
			enterTransition = {
				scaleIn(
					initialScale = 0.8f,
					transformOrigin = TransformOrigin.Center,
					animationSpec = tween(
						durationMillis = 1000,
						delayMillis = 10
					)
				) + fadeIn(
					initialAlpha = 0.0f,
					animationSpec = tween(
						durationMillis = 1000,
						delayMillis = 10
					)
				) + slideIntoContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Up,
					animationSpec = tween(
						durationMillis = 300,
						delayMillis = 10
					)
				)
			},
			exitTransition = {
				scaleOut(
					targetScale = 0.8f,
					transformOrigin = TransformOrigin.Center,
					animationSpec = tween(
						durationMillis = 1000,
						delayMillis = 10
					)
				) + fadeOut(
					targetAlpha = 0.0f,
					animationSpec = tween(
						durationMillis = 1000,
						delayMillis = 10
					)
				) +  slideOutOfContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Down,
					animationSpec = tween(
						durationMillis = 300,
						delayMillis = 10
					)
				)
			}
		) {
			SongListScreen()
		}

		composable(route = Screen.Search.route) {
			SearchScreen()
		}

		composable(route = Screen.Library.route) {
			LibraryScreen()
		}

		composable(
			route = Screen.NowPlaying.route,
			enterTransition = {
				slideIntoContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Up,
					animationSpec = tween(
						durationMillis = 1000,
						delayMillis = 10
					)
				)
			},
			exitTransition = {
				slideOutOfContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Down,
					animationSpec = tween(
						durationMillis = 1000,
						delayMillis = 10
					)
				)
			}
		) {
//			PlayingScreen()
			AnotherPlayingScreen {
				navController.navigateUp()
			}
		}

	}

}