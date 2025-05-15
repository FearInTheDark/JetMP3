package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.UIEvent
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.ui.components.home.RecentCategory
import com.vincent.jetmp3.ui.components.home.RecentScroll
import com.vincent.jetmp3.ui.components.home.TrackSelect
import com.vincent.jetmp3.ui.layout.LoadingHolder
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.TitleLineLarge
import com.vincent.jetmp3.ui.viewmodels.AudioViewModel
import com.vincent.jetmp3.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
	viewModel: AudioViewModel = hiltViewModel(),
	homeViewModel: HomeViewModel = hiltViewModel(),
	action: () -> Unit = {}
) {

	val coroutineScope = rememberCoroutineScope()
	val tracks by viewModel.tracks

	var dropdownShow by remember { mutableStateOf(false) }
	val refreshing by remember(viewModel.uiState) {
		derivedStateOf {
			viewModel.uiState.value == UIState.Fetching
		}
	}

	PullToRefreshBox(
		isRefreshing = refreshing,
		onRefresh = { viewModel.onUiEvent(UIEvent.FetchAudio) },
	) {
		Column(
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.surface),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {

			TopAppBar(
				title = @Composable {
					TextButton(
						onClick = { dropdownShow = true },
						colors = ButtonColors(
							containerColor = Color.Transparent,
							contentColor = MaterialTheme.colorScheme.onSurface,
							disabledContainerColor = Color.Transparent,
							disabledContentColor = Color.Gray
						),
					) {
						Text(
							text = stringResource(R.string.app_name),
							style = HeadStyleLarge
						)
					}

					DropdownMenu(
						expanded = dropdownShow,
						onDismissRequest = { dropdownShow = false },
						shape = RoundedCornerShape(10.dp),
						offset = DpOffset(x = 0.dp, y = 0.dp),
						modifier = Modifier.width(200.dp)
					) {
						DropdownMenuItem(
							text = { Text("Explore", style = TitleLineLarge) },
							leadingIcon = {
								Icon(
									painter = painterResource(R.drawable.material_icon_theme__gemini_ai),
									null,
									tint = Color.Unspecified,
									modifier = Modifier.size(20.dp)
								)
							},
							onClick = {}
						)
						DropdownMenuItem(
							text = { Text("Custom", style = TitleLineLarge) },
							leadingIcon = {
								Icon(
									painter = painterResource(R.drawable.bxs__customize),
									null,
									tint = Color.Unspecified
								)
							},
							onClick = {}
						)
						DropdownMenuItem(
							text = { Text("Local", style = TitleLineLarge, textAlign = TextAlign.End) },
							leadingIcon = {
								Icon(
									painter = painterResource(R.drawable.line_md__download),
									null,
									tint = Color.Unspecified
								)
							},
							onClick = {}
						)
					}
				},
				actions = {
					IconButton(
						onClick = { coroutineScope.launch { homeViewModel.logout(); action() } }
					) {

						Icon(
							imageVector = Icons.Default.MusicNote,
							contentDescription = "Now Playing",
							modifier = Modifier
								.padding(8.dp)
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = Color.Unspecified.copy(0.6f)
				)
			)

			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp)),
				contentPadding = PaddingValues(
					top = 12.dp,
					bottom = 150.dp
				),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {

				item {
					RecentCategory()
					Spacer(Modifier.height(8.dp))
				}

				item {
					RecentScroll()
					Spacer(Modifier.height(8.dp))
				}

				if (tracks.isEmpty()) {
					item {
						Box(
							modifier = Modifier.fillMaxWidth(),
							contentAlignment = Alignment.Center
						) {
							LoadingHolder(icon = "loading-plane.json", modifier = Modifier.size(300.dp))
						}
					}
				} else {
					itemsIndexed(tracks) { index, track ->
						TrackSelect(track) {
							viewModel.setTracks(index = index)
							viewModel.onUiEvent(UIEvent.PlayPause)
							viewModel.startService()
						}
					}
				}
			}
		}
	}
}
