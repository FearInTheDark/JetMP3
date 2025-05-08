package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.vincent.jetmp3.domain.NestService
import com.vincent.jetmp3.ui.theme.TitleLineBig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun RecentScroll(
	recentViewModel: RecentViewModel = hiltViewModel()
) {
	val recentlyPlayed = listOf(
		PlaylistItem("Without Me", "without_me", PlaylistType.RADIO, "Billie Eilish, Tate McRae, benny blanco, Halsey"),
		PlaylistItem("Lana Del Rey", "lana_del_rey", PlaylistType.ARTIST),
		PlaylistItem("Daily Mix", "daily_mix", PlaylistType.PLAYLIST)
	)

	Column(
		modifier = Modifier
			.fillMaxWidth(),
		horizontalAlignment = Alignment.Start,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = "History",
			style = TitleLineBig,
			fontSize = 24.sp,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.padding(horizontal = 12.dp)
		)

		LazyRow(
			contentPadding = PaddingValues(horizontal = 0.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			item { Spacer(Modifier.width(4.dp)) }
			items(recentlyPlayed) { playlist ->
				RecentlyPlayedItem(
					playlist = playlist,
				)
			}
			item { Spacer(Modifier.width(4.dp)) }
		}
	}
}

@HiltViewModel
class RecentViewModel @Inject constructor(
	private val nestService: NestService
) : ViewModel()