package com.vincent.jetmp3.utils

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Library : Screen("library")
    data object Settings : Screen("settings")
    data object NowPlaying : Screen("now_playing")
    data object SplashScreen : Screen("splash_screen")
    data object PlayerScreen : Screen("player_screen")
    data object Auth : Screen("auth_screen")
}

data class NavigationBarItem(
    val route: String,
    val title: String,
    val icon: Int,
    val activeIcon: Int,
)