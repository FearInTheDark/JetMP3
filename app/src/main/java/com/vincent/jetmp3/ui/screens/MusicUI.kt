package com.vincent.jetmp3.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.UIEvent
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.ui.components.home.RecentCategory
import com.vincent.jetmp3.ui.components.home.RecentScroll
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.ui.theme.LabelLineSmall
import com.vincent.jetmp3.ui.theme.TitleLineLarge
import com.vincent.jetmp3.ui.viewmodels.AudioViewModel
import com.vincent.jetmp3.ui.viewmodels.HomeViewModel
import com.vincent.jetmp3.utils.RecentCategoryItem
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.formatDuration

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
	viewModel: AudioViewModel = hiltViewModel(),
	homeViewModel: HomeViewModel = hiltViewModel(),
	onItemClick: () -> Unit,
	action: () -> Unit = {}
) {

	val coroutineScope = rememberCoroutineScope()
	val playbackState by viewModel.playbackState.collectAsState()

	val refreshing by remember(viewModel.uiState) {
		derivedStateOf {
			viewModel.uiState.value == UIState.Fetching
		}
	}
	var dropdownShow by remember { mutableStateOf(false) }
	val context = LocalContext.current

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
							text = "JetMP3",
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
					RecentCategory(
						categories = listOf(
							RecentCategoryItem(),
							RecentCategoryItem(),
							RecentCategoryItem(),
							RecentCategoryItem(),
							RecentCategoryItem(),
							RecentCategoryItem(),
							RecentCategoryItem(),
						)
					)
				}

				item {
					Spacer(Modifier.height(8.dp))
				}

				item {
					RecentScroll()
				}

				item {
					Spacer(Modifier.height(8.dp))
				}

				item {
					RecentScroll()
				}

				item {
					Spacer(Modifier.height(8.dp))
				}

				if (playbackState.queue.isEmpty()) {
					item {
						Box(
							modifier = Modifier.fillMaxWidth(),
							contentAlignment = Alignment.Center
						) {
							Text("Đang tải danh sách bài hát...", color = MaterialTheme.colorScheme.onSurface)
						}
					}
				} else {
					itemsIndexed(playbackState.queue) { index, track ->
						Box(
							modifier = Modifier
								.fillMaxWidth()
								.padding(horizontal = 8.dp)
								.clickable {
									viewModel.onUiEvent(UIEvent.SelectedAudioChange(index))
									onItemClick()
								},
						) {
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.padding(4.dp),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.SpaceBetween
							) {
								Row(
									verticalAlignment = Alignment.CenterVertically,
									horizontalArrangement = Arrangement.spacedBy(12.dp)
								) {
									Box(
										modifier = Modifier
											.size(50.dp)
									) {
										// Placeholder for album art or song icon
										AsyncImage(
											model = ImageRequest.Builder(context)
												.data(track.images.first()).build(),
											fallback = painterResource(R.drawable.material_icon_theme__gemini_ai),
											contentDescription = null,
											modifier = Modifier
												.clip(RoundedCornerShape(4.dp))
												.align(Alignment.Center),
											contentScale = ContentScale.Crop,
										)
									}

									Column(
										verticalArrangement = Arrangement.spacedBy(2.dp),
										modifier = Modifier.widthIn(max = 220.dp)
									) {
										Text(
											text = track.name,
											style = LabelLineMedium,
											fontWeight = FontWeight.Normal,
											color = MaterialTheme.colorScheme.onSurface,
											maxLines = 1,
											overflow = TextOverflow.Ellipsis
										)
										Text(
											text = track.artistType.name,
											style = LabelLineSmall,
											color = MaterialTheme.colorScheme.onSurfaceVariant,
											maxLines = 1,
											overflow = TextOverflow.Ellipsis
										)
									}
								}

								// Optional: Duration or play icon
								IconButton(
									onClick = {}
								) {
									Icon(
										imageVector = Icons.Default.MusicNote,
										contentDescription = null,
										tint = MaterialTheme.colorScheme.onSurface
									)
								}
							}
						}
					}
				}
			}
		}
	}
}
