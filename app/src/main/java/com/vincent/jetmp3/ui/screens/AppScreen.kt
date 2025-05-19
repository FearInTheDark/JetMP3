package com.vincent.jetmp3.ui.screens

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vincent.jetmp3.data.constants.SheetContentType
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.ui.components.home.CustomSheetContent
import com.vincent.jetmp3.ui.components.navigation.MyNavigationBar
import com.vincent.jetmp3.ui.components.navigation.NowPlayingBar
import com.vincent.jetmp3.ui.screens.auth.AuthScreen
import com.vincent.jetmp3.ui.screens.auth.AuthWelcome
import com.vincent.jetmp3.ui.screens.test.FullScreenSwipeToDismiss
import com.vincent.jetmp3.ui.state.LocalBottomSheetState
import com.vincent.jetmp3.ui.state.LocalPlayingShow
import com.vincent.jetmp3.ui.state.LocalSelectedCategory
import com.vincent.jetmp3.ui.state.LocalSelectedTrack
import com.vincent.jetmp3.ui.state.LocalSheetContentType
import com.vincent.jetmp3.ui.state.LocalSnackBarHostState
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.ui.viewmodels.AppScreenViewModel
import com.vincent.jetmp3.utils.Screen

@Composable
@ExperimentalMaterial3Api
@ExperimentalPermissionsApi
@ExperimentalMaterial3ExpressiveApi
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun AppScreen(
	viewModel: AppScreenViewModel = hiltViewModel()
) {
	val navController = rememberNavController()
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute = navBackStackEntry?.destination?.route

	val permissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_AUDIO)

	val networkStatus = viewModel.networkStatus.collectAsState()
	val selectedTrack = remember { mutableStateOf<Track?>(null) }
	val selectedCategory = remember { mutableStateOf("") }
	val playingShow = remember { mutableStateOf(false) }
	val sheetContentType = remember { mutableStateOf(SheetContentType.OPTIONS) }
	val snackbarHostState = remember { SnackbarHostState() }
	val standardBottomSheet = rememberStandardBottomSheetState(
		initialValue = SheetValue.PartiallyExpanded,
	)
	val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
		bottomSheetState = standardBottomSheet,
		snackbarHostState = snackbarHostState,
	)

	val showNavBar by remember(currentRoute) {
		derivedStateOf {
			currentRoute !in listOf(Screen.Auth.route, Screen.AuthWelcome.route, Screen.NowPlaying.route)
		}
	}

	LaunchedEffect(Unit) {
		if (!permissionState.status.isGranted) {
			permissionState.launchPermissionRequest()
		}
	}

	CompositionLocalProvider(
		LocalTextStyle provides LabelLineMedium,
		LocalPlayingShow provides playingShow,
		LocalSelectedTrack provides selectedTrack,
		LocalSelectedCategory provides selectedCategory,
		LocalSnackBarHostState provides snackbarHostState,
		LocalSheetContentType provides sheetContentType,
		LocalBottomSheetState provides bottomSheetScaffoldState,
	) {
		BottomSheetScaffold(
			scaffoldState = bottomSheetScaffoldState,
			sheetContent = { CustomSheetContent() },
			sheetContainerColor = MaterialTheme.colorScheme.surfaceDim,
			sheetContentColor = MaterialTheme.colorScheme.onSurface,
			sheetPeekHeight = 0.dp,
			sheetShape = RoundedCornerShape(8.dp),
			snackbarHost = {
				SnackbarHost(
					hostState = snackbarHostState,
					modifier = Modifier.offset(y = ((if (showNavBar) -130 else -100).dp))
				)
			}
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(MaterialTheme.colorScheme.surface),
				contentAlignment = Alignment.Center,
			) {
				AppNavHost(navController, networkStatus)

				if (showNavBar) {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.height(100.dp)
							.align(Alignment.BottomCenter)
							.background(
								Brush.verticalGradient(
									colorStops = arrayOf(
										0f to Color.Transparent,
										1f to Color.Black.copy(0.9f),
									),
								)
							)
					)

					Column(
						modifier = Modifier
							.fillMaxSize(),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.Bottom
					) {
						NowPlayingBar { playingShow.value = !playingShow.value }
						MyNavigationBar(
							modifier = Modifier
								.fillMaxWidth()
								.background(
									Brush.verticalGradient(
										listOf(
											Color.Transparent,
											Color.Black.copy(0.7f),
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

			AnimatedVisibility(
				visible = playingShow.value,
				enter = slideInVertically(
					initialOffsetY = { it }, // From bottom to top
					animationSpec = tween(durationMillis = 1000, delayMillis = 10)
				),
				exit = slideOutVertically(
					targetOffsetY = { it }, // To bottom
					animationSpec = tween(durationMillis = 1000, delayMillis = 10)
				)
			) {
				FullScreenSwipeToDismiss(
					onDismissed = { playingShow.value = !playingShow.value }
				) {
					PlayingScreen {
						playingShow.value = !playingShow.value
					}
				}
			}
		}
	}
}

@ExperimentalMaterial3Api
@ExperimentalMaterial3ExpressiveApi
@Composable
fun AppNavHost(
	navController: NavHostController,
	networkStatus: State<Boolean>
) {
	NavHost(
		navController = navController,
		startDestination = Screen.AuthWelcome.route,
		route = "main_graph",
	) {
		composable(
			route = Screen.AuthWelcome.route,
			enterTransition = {
				slideIntoContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Down,
					animationSpec = tween(
						durationMillis = 500,
						delayMillis = 10
					)
				)
			},
		) {
			AuthWelcome(
				onSignInAction = { navController.navigate(Screen.Auth.route) },
				onSignUpAction = { navController.navigate(Screen.Auth.route) },
				onValidated = {
					navController.navigate(if (networkStatus.value) Screen.Home.route else Screen.Library.route) {
						popUpTo(navController.graph.startDestinationId) { inclusive = true }
						launchSingleTop = true
					}
				}
			)
		}

		composable(
			route = Screen.Auth.route,
			enterTransition = {
				slideIntoContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Left,
					animationSpec = tween(
						durationMillis = 500,
						delayMillis = 10
					)
				)
			},
			exitTransition = {
				slideOutOfContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Down,
					animationSpec = tween(
						durationMillis = 500,
						delayMillis = 10
					)
				)
			}
		) {
			AuthScreen {
				navController.navigate(Screen.Home.route) {
					popUpTo(navController.graph.startDestinationId) { inclusive = true }
					launchSingleTop = true
				}
			}
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
				) + slideOutOfContainer(
					towards = AnimatedContentTransitionScope.SlideDirection.Down,
					animationSpec = tween(
						durationMillis = 300,
						delayMillis = 10
					)
				)
			}
		) {
			SongListScreen {
				navController.navigate(Screen.AuthWelcome.route) {
					popUpTo(Screen.Home.route) { inclusive = true }
					launchSingleTop = true
				}
			}
		}

		composable(route = Screen.Search.route) {
			SearchScreen()
		}

		composable(route = Screen.Library.route) {
			LibraryScreen()
		}
	}
}