package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.repository.NestRepository
import com.vincent.jetmp3.ui.theme.HeadLineMedium
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineBold
import com.vincent.jetmp3.ui.theme.TitleLineBig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- Data Model ---
data class Playlist(
	val id: Int,
	val name: String,
	val imageUrl: String? = null,
	val trackIds: List<Long> = emptyList()
)

// --- Composable Screen ---
@Composable
fun AddToPlaylistScreen(
	currentTrackId: Long,
	trackName: String,
	viewModel: AddToPlaylistViewModel = hiltViewModel(),
	onBackClick: () -> Unit = {}
) {

	val playlists by viewModel.playlists.collectAsState()
	val newPlaylistName by viewModel.newPlaylistName.collectAsState()
	val uiState by viewModel.uiState.collectAsState()
	var isCreating by remember { mutableStateOf(false) }

	LaunchedEffect(Unit) {
		viewModel.clear()
		viewModel.fetchPlaylists()
		viewModel.setTrackId(currentTrackId)
	}

	LazyColumn(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(12.dp))
			.background(MaterialTheme.colorScheme.background)
	) {
		item {
			TopAppBar(onBackClick = onBackClick, trackName = trackName)
		}

		item {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 24.dp, vertical = 16.dp),
				contentAlignment = Alignment.Center
			) {
				if (!isCreating) {
					Button(
						onClick = { isCreating = true },
						shape = RoundedCornerShape(50),
						colors = ButtonDefaults.buttonColors(
							containerColor = Color.White,
							contentColor = Color.Black
						),
						modifier = Modifier
							.fillMaxWidth()
							.height(48.dp)
					) {
						Text("Create a new Playlist", style = TitleLineBig)
					}
				} else {
					Column(
						modifier = Modifier.fillMaxWidth(),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(4.dp)
					) {
						TextField(
							value = newPlaylistName,
							onValueChange = { viewModel.setNewPlaylistName(it) },
							placeholder = { Text("Playlist name", style = TitleLineBig) },
							textStyle = TitleLineBig
						)

						Row(verticalAlignment = Alignment.CenterVertically) {
							Button(
								onClick = { isCreating = false },
								colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
							) {
								Text("Cancel", style = TitleLineBig, fontSize = 16.sp, color = Color.White)
							}
							TextButton(
								onClick = {
									viewModel.addTrackToNewPlaylist(currentTrackId)
									onBackClick()
								},
								shape = CircleShape,
								colors = ButtonDefaults.buttonColors(
									containerColor = Color(0xFF1DB954),
									contentColor = Color.Black
								),
								modifier = Modifier.padding(vertical = 8.dp),
								contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
							) {
								Text("Add", style = TitleLineBig, fontSize = 16.sp)
							}
						}

						HorizontalDivider()
					}
				}
			}
		}

		when (uiState) {
			UIState.Fetching -> @Composable {
				item {
					Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
						CircularProgressIndicator()
					}
				}
			}

			else -> @Composable {

				if (playlists.isNotEmpty()) {
					item {
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
						) {
							Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = Color.White)
							Spacer(modifier = Modifier.width(8.dp))
							Text("Most relevant playlists", style = HeadLineMedium, fontSize = 18.sp)
						}
					}

					items(playlists) { playlist ->
						PlaylistItem(
							playlist = playlist,
							trackId = currentTrackId,
							onSelectChange = {
								viewModel.toggleTrackInPlaylist(playlist.id)
							}
						)
						Spacer(modifier = Modifier.height(16.dp))
					}
				}
			}
		}

		item {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				contentAlignment = Alignment.Center
			) {
				Button(
					onClick = { viewModel.handle(); onBackClick() },
					shape = CircleShape,
					colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFF1DB954),
						contentColor = Color.Black
					),
					modifier = Modifier.padding(vertical = 8.dp),
					contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
				) {
					Text("Done", style = TitleLineBig, fontSize = 16.sp)
				}
			}
		}
	}
}

@Composable
fun PlaylistItem(playlist: Playlist, trackId: Long, onSelectChange: () -> Unit) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	) {
		Box(
			modifier = Modifier
				.size(48.dp)
				.clip(RoundedCornerShape(8.dp))
		) {
			AsyncImage(
				model = playlist.imageUrl,
				fallback = painterResource(R.drawable.hugeicons__playlist_01),
				contentDescription = null,
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxSize()
			)
		}

		Column(
			modifier = Modifier
				.weight(1f)
				.padding(start = 16.dp)
		) {
			Text(
				text = playlist.name,
				style = HeadStyleLarge,
				fontSize = 16.sp,
				fontWeight = FontWeight.SemiBold
			)
			Text(
				text = "${playlist.trackIds.size} tracks",
				style = LabelLineBold,
				color = Color.Gray,
				fontSize = 14.sp
			)
		}

		RadioButton(
			selected = playlist.trackIds.contains(trackId),
			onClick = onSelectChange,
			colors = RadioButtonDefaults.colors(
				selectedColor = Color.White,
				unselectedColor = Color.Gray
			)
		)
	}
}

@Composable
fun TopAppBar(onBackClick: () -> Unit, trackName: String) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 16.dp)
	) {
		IconButton(onClick = onBackClick) {
			Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
		}
		Spacer(modifier = Modifier.width(8.dp))
		Text(
			text = buildAnnotatedString {
				append("Adding ")
				withStyle(style = SpanStyle(color = Color(0xFF1891FC))) { append(trackName) }
				append(" to Playlist")
			},
			style = HeadLineMedium,
			fontSize = 20.sp,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.basicMarquee()
		)
	}
}

// --- ViewModel ---
@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
	private val nestRepository: NestRepository
) : ViewModel() {

	private val _changedPlaylistIds = MutableStateFlow<MutableSet<Int>>(mutableSetOf())

	private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
	val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

	private val _newPlaylistName = MutableStateFlow("")
	val newPlaylistName: StateFlow<String> = _newPlaylistName.asStateFlow()

	private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Fetching)
	val uiState = _uiState.asStateFlow()

	private val _trackId = MutableStateFlow(0L)

	fun setNewPlaylistName(name: String) {
		_newPlaylistName.value = name
	}

	fun fetchPlaylists() = viewModelScope.launch {
		_uiState.value = UIState.Fetching
		val categories = nestRepository.getRecentCategories()
		val all = buildList {
			if (categories != null) {
				add(
					Playlist(
						id = categories.favorite.id,
						name = categories.favorite.title,
						imageUrl = categories.favorite.iconUri,
						trackIds = categories.favorite.trackIds
					)
				)
				addAll(categories.playlists.map {
					Playlist(
						id = it.id,
						name = it.title,
						imageUrl = it.iconUri,
						trackIds = it.trackIds
					)
				})
			}
		}
		_playlists.value = all
		_uiState.value = UIState.Ready
	}

	fun toggleTrackInPlaylist(playlistId: Int) {
		_playlists.value = _playlists.value.map { playlist ->
			if (playlist.id == playlistId) {
				val updatedTracks = playlist.trackIds.toMutableList().apply {
					if (contains(_trackId.value)) remove(_trackId.value) else add(_trackId.value)
				}
				playlist.copy(trackIds = updatedTracks)
			} else playlist
		}

		val current = _changedPlaylistIds.value.toMutableSet()
		if (!current.add(playlistId)) current.remove(playlistId) // toggle lại -> remove
		_changedPlaylistIds.value = current
	}

	fun addTrackToNewPlaylist(trackId: Long) = viewModelScope.launch {
		nestRepository.addTrackToNewPlaylist(_newPlaylistName.value, trackId)
	}

	fun handle() = viewModelScope.launch {
		println(_changedPlaylistIds.value)
		_changedPlaylistIds.value.forEach { playlistId ->
			if (playlistId == _playlists.value.firstOrNull()?.id) {
				nestRepository.toggleFavorite(_trackId.value)
				println("Toggle favorite: ${_trackId.value}")
			} else {
				nestRepository.toggleTrackToPlaylist(playlistId, _trackId.value)
				println("Toggle track to playlist: $playlistId, ${_trackId.value}")
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		_playlists.value = emptyList()
		_newPlaylistName.value = ""
	}

	fun setTrackId(currentTrackId: Long) {
		_trackId.value = currentTrackId
	}

	fun clear() {
		_playlists.value = emptyList()
		_newPlaylistName.value = ""
		_changedPlaylistIds.value.clear()
		_uiState.value = UIState.Fetching
		_trackId.value = 0L
	}
}
