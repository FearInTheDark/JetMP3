package com.vincent.jetmp3.ui.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.SheetContentType
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.ui.components.home.RecentCategory
import com.vincent.jetmp3.ui.components.home.RecentScroll
import com.vincent.jetmp3.ui.components.home.TrackSelect
import com.vincent.jetmp3.ui.state.LocalBottomSheetState
import com.vincent.jetmp3.ui.state.LocalSelectedTrack
import com.vincent.jetmp3.ui.state.LocalSheetContentType
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.TitleLineBig
import com.vincent.jetmp3.ui.theme.TitleLineLarge
import com.vincent.jetmp3.ui.viewmodels.AudioViewModel
import com.vincent.jetmp3.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@Composable
@UnstableApi
@ExperimentalMaterial3Api
@ExperimentalMaterial3ExpressiveApi
fun SongListScreen(
	viewModel: AudioViewModel = hiltViewModel(),
	homeViewModel: HomeViewModel = hiltViewModel(),
	action: () -> Unit = {}
) {
	val scope = rememberCoroutineScope()
	val infiniteTransition = rememberInfiniteTransition(label = "colorTransition")

	val selectedTrack = LocalSelectedTrack.current
	val sheetContentType = LocalSheetContentType.current
	val bottomSheetScaffoldState = LocalBottomSheetState.current

	val tracks by viewModel.tracks
	val uiState by viewModel.uiState.collectAsState()
	val refreshing = uiState == UIState.Fetching

	var dropdownShow by remember { mutableStateOf(false) }
	var layerShow by remember { mutableStateOf(false) }

	val animatedColor by if (uiState == UIState.Fetching) {
		infiniteTransition.animateColor(
			initialValue = Color.Red,
			targetValue = Color.Red,
			animationSpec = infiniteRepeatable(
				animation = keyframes {
					durationMillis = 9000
					Color(0xFFF64E4E) at 0
					Color(0xFFFFF823) at 3000
					Color(0x9054A0FF) at 6000
					Color.Red at 9000
				},
				repeatMode = RepeatMode.Restart
			),
			label = "animatedColor"
		)
	} else {
		rememberUpdatedState(Color.Blue)
	}

	PullToRefreshBox(
		isRefreshing = refreshing,
		onRefresh = { viewModel.fetchTracks() },
	) {
		Column(
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {

			TopAppBar(
				title = @Composable {
					TextButton(
						onClick = { },
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
						onClick = { scope.launch { homeViewModel.logout(); action() } }
					) {

						Icon(
							imageVector = Icons.AutoMirrored.Filled.Logout,
							contentDescription = "Now Playing",
							modifier = Modifier
								.padding(8.dp)
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.background
				)
			)

			when (uiState) {
				UIState.Fetching -> @Composable {
					Box(
						modifier = Modifier.fillMaxWidth(),
						contentAlignment = Alignment.Center,
						content = {
							Column(
								Modifier.fillMaxWidth(),
								horizontalAlignment = Alignment.CenterHorizontally,
								verticalArrangement = Arrangement.spacedBy(8.dp)
							) {
								LoadingIndicator(
									modifier = Modifier.size(300.dp),
									color = animatedColor
								)
								Text(
									text = "Fetching Data",
									style = HeadStyleLarge,
									color = MaterialTheme.colorScheme.onSurface,
									modifier = Modifier.padding(16.dp)
								)
							}
						}
					)
				}

				else -> @Composable {
					LazyColumn(
						modifier = Modifier
							.fillMaxSize(),
						contentPadding = PaddingValues(
							top = 12.dp,
							bottom = 150.dp
						),
						verticalArrangement = Arrangement.spacedBy(8.dp)
					) {

						item {
							RecentCategory {
								layerShow = !layerShow
							}
							Spacer(Modifier.height(8.dp))
						}

						item {
							RecentScroll()
							Spacer(Modifier.height(8.dp))
						}

						item {
							Text(
								text = "All Tracks",
								style = TitleLineBig,
								fontSize = 28.sp,
								color = MaterialTheme.colorScheme.onSurface,
								modifier = Modifier.padding(horizontal = 12.dp)
							)
						}
						itemsIndexed(tracks) { index, track ->
							TrackSelect(
								track = track,
								onOptionClick = {
									scope.launch {
										selectedTrack.value = track
										sheetContentType.value = SheetContentType.OPTIONS
										bottomSheetScaffoldState.bottomSheetState.expand()
									}
								}
							) {
								viewModel.setTracks(index = index)
							}
						}
					}
				}
			}
		}
		CategoryScreen(
			show = layerShow,
		) { layerShow = false }
	}
}


