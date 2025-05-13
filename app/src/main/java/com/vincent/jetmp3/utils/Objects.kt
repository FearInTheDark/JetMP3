package com.vincent.jetmp3.utils

import androidx.media3.common.MediaItem
import arrow.core.Either
import com.vincent.jetmp3.data.models.NestArtist
import com.vincent.jetmp3.data.models.SpotifyArtist
import com.vincent.jetmp3.data.models.Track

sealed class Screen(val route: String) {
	data object Home : Screen("home")
	data object Search : Screen("search")
	data object Library : Screen("library")
	data object Settings : Screen("settings")
	data object NowPlaying : Screen("now_playing")
	data object AuthWelcome : Screen("auth_welcome")
	data object Auth : Screen("auth_screen")
}

data class NavigationBarItem(
	val route: String,
	val title: String,
	val icon: Int,
	val activeIcon: Int,
)

data class PlaybackState(
	var isPlaying: Boolean = false,
	val currentIndex: Int = 0,
	val currentPosition: Long = 0L,
	val duration: Long = 0L,
	val bufferedPosition: Long = 0L,
	var progress: Float = 0f,
	var trackList: List<MediaItem> = emptyList(),
	val currentMediaItem: MediaItem? = null,
	var queue: List<Track> = emptyList(),
	var currentTrack: Track? = null,
	val currentArtist: Either<NestArtist?, SpotifyArtist?> = Either.Left(null),
	val isBuffering: Boolean = false,
	val hasEnded: Boolean = false
)

data class RecentCategoryItem(
	val imageUrl: String = "",
	val title: String = ""
)

