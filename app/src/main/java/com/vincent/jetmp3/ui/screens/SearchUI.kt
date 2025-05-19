package com.vincent.jetmp3.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.ui.components.home.ArtistSearchSelect
import com.vincent.jetmp3.ui.components.home.TrackSelect
import com.vincent.jetmp3.ui.layout.LoadingOverlay
import com.vincent.jetmp3.ui.theme.HeadLineMedium
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.ui.theme.TitleLineBig
import com.vincent.jetmp3.ui.viewmodels.SearchViewModel
import com.vincent.jetmp3.utils.functions.fadingEdge
import kotlinx.coroutines.launch

@UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {

	var expanded by rememberSaveable { mutableStateOf(false) }
	val searchQuery by viewModel.searchQuery.collectAsState()
	val searchResult by viewModel.result.collectAsState()
	val uiState by viewModel.uiState.collectAsState()
	val screenHeight = LocalConfiguration.current.screenHeightDp.dp
	val scope = rememberCoroutineScope()

	LazyColumn(
		Modifier
			.fillMaxSize()
			.padding(4.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {

		item {
			AnimatedVisibility(
				visible = !expanded,
				enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(500)),
				exit = slideOutVertically(
					targetOffsetY = { -it },
					animationSpec = tween(500)
				)
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
								text = "Search",
								style = HeadStyleLarge
							)
						}
					},
					actions = {
						IconButton(
							onClick = { }
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
						containerColor = Color.Transparent
					),

				)
			}
		}

		stickyHeader {
			Box(
				modifier = Modifier
					.zIndex(1f)
					.fillMaxWidth()
					.heightIn(max = screenHeight),
				contentAlignment = Alignment.Center
			) {
				SearchBar(
					expanded = expanded,
					onExpandedChange = { expanded = it },
					shape = RoundedCornerShape(10.dp),
					colors = SearchBarDefaults.colors(
						containerColor = MaterialTheme.colorScheme.background,
						dividerColor = MaterialTheme.colorScheme.onBackground,
					),
					inputField = @Composable {
						SearchBarDefaults.InputField(
							query = searchQuery,
							expanded = expanded,
							onQueryChange = { viewModel.setSearchQuery(it) },
							onSearch = {},
							onExpandedChange = { expanded = it },
							leadingIcon = {
								Icon(
									painter = painterResource(if (expanded) R.drawable.mingcute__search_fill else R.drawable.tabler__search),
									null
								)
							},
							trailingIcon = {
								if (uiState == UIState.Fetching) {
									CircularProgressIndicator(
										trackColor = Color.Transparent,
										color = MaterialTheme.colorScheme.onSurface,
										strokeWidth = 3.dp,
										modifier = Modifier.size(25.dp)
									)
								} else
									IconButton(
										onClick = { viewModel.clear() },
										enabled = searchQuery.isNotEmpty()
									) {
										Icon(
											imageVector = if (searchQuery.isNotEmpty()) Icons.Default.Close else Icons.Default.MusicNote,
											null
										)
									}
							},
							placeholder = {
								Text(
									text = "Search For Everything",
									style = LabelLineMedium,
									fontWeight = FontWeight.Normal,
									color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
								)
							},
							colors = TextFieldDefaults.colors(
								focusedTextColor = MaterialTheme.colorScheme.onSurface,
								focusedContainerColor = MaterialTheme.colorScheme.background,
								disabledContainerColor = MaterialTheme.colorScheme.background,
								unfocusedContainerColor = MaterialTheme.colorScheme.background,
							),
						)
					},
				) {
					searchResult?.tracks?.let { tracks ->
						Column(
							modifier = Modifier
								.verticalScroll(rememberScrollState())
								.padding(vertical = 8.dp, horizontal = 4.dp)
								.background(
									color = MaterialTheme.colorScheme.surface,
									shape = RoundedCornerShape(8.dp)
								)
								.padding(vertical = 8.dp)
						) {
							tracks.forEachIndexed { index, track ->
								TrackSelect(track) { scope.launch { viewModel.prepareAndPlay(index = index) } }
							}
							searchResult!!.artists?.forEach { artist ->
								ArtistSearchSelect(artist)
							}
						}
					}
				}
			}
		}

		item {
			when (uiState) {
				UIState.Fetching -> @Composable {
					LoadingOverlay(
						isLoading = true,
						icon = "searching.lottie",
						iconModifier = Modifier.size(400.dp)
					)
				}

				UIState.Ready -> @Composable {
					if (searchResult?.tracks.isNullOrEmpty()) {
						LoadingOverlay(
							isLoading = true,
							backgroundColor = Color.Transparent,
							icon = "empty.lottie",
							iconModifier = Modifier
								.size(400.dp)
						)
						Text(
							text = "No Results Found!",
							style = HeadLineMedium,
							color = MaterialTheme.colorScheme.onSurface
						)
					} else {
						FlowRow(
							modifier = Modifier
								.fillMaxWidth()
								.offset(y = 100.dp),
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalArrangement = Arrangement.spacedBy(8.dp),
							itemVerticalAlignment = Alignment.CenterVertically,
							maxItemsInEachRow = 2,
						) {

							searchResult?.tracks?.forEachIndexed { index, track ->
								Box(
									modifier = Modifier
										.weight(1f)
										.aspectRatio(1f)
										.clip(RoundedCornerShape(4.dp))
										.clickable { scope.launch { viewModel.prepareAndPlay(index = index) } },
									contentAlignment = Alignment.Center
								) {
									AsyncImage(
										model = track.images.firstOrNull(),
										fallback = painterResource(R.drawable.material_icon_theme__gemini_ai),
										contentScale = ContentScale.Fit,
										contentDescription = "Track Image",
										modifier = Modifier
											.zIndex(1f)
											.fillMaxSize()
											.fadingEdge(
												Brush.verticalGradient(
													0.6f to Color.Black.copy(0.8f),
													1f to Color.Black.copy(0.05f)
												)
											)
									)

									Text(
										text = track.name,
										style = TitleLineBig,
										fontSize = 20.sp,
										textAlign = TextAlign.Center,
										letterSpacing = (-1).sp,
										maxLines = 1,
										overflow = TextOverflow.Ellipsis,
										color = MaterialTheme.colorScheme.onSurface.copy(0.92f),
										modifier = Modifier
											.zIndex(1f)
											.align(Alignment.BottomCenter)
											.fillMaxWidth(0.95f)
											.basicMarquee()
									)
								}
							}
						}
					}
				}

				else -> @Composable {
				}
			}
		}

		item { Spacer(Modifier.height(200.dp)) }
	}
}