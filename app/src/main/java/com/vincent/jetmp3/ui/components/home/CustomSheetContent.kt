package com.vincent.jetmp3.ui.components.home

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.SheetContentType
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.repository.NestRepository
import com.vincent.jetmp3.domain.models.CategoryScreenData
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.ui.state.LocalSelectedCategory
import com.vincent.jetmp3.ui.state.LocalSelectedTrack
import com.vincent.jetmp3.ui.state.LocalSheetContentType
import com.vincent.jetmp3.ui.theme.HeadLineMedium
import com.vincent.jetmp3.utils.OptionValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
@Composable
fun CustomSheetContent(
	modifier: Modifier = Modifier,
	viewModel: CustomSheetViewModel = hiltViewModel(),
) {
	val playbackState by viewModel.playbackState.collectAsState()
	val screenData by viewModel.screenData.collectAsState()

	val screenHeight = LocalConfiguration.current.screenHeightDp.dp

	val selectedTrack = LocalSelectedTrack.current
	val selectedCategory = LocalSelectedCategory.current
	val sheetContentType = LocalSheetContentType.current

	LaunchedEffect(selectedCategory.value) {
		viewModel.fetchCategoryData(selectedCategory.value)
	}

	val title = when (sheetContentType.value) {
		SheetContentType.QUEUE -> "Queue"
		SheetContentType.OPTIONS -> "Options"
		SheetContentType.CATEGORY_OPTION -> "Category Options"
	}

	LazyColumn(
		Modifier
			.fillMaxWidth()
			.heightIn(max = screenHeight / 1.5f)
			.padding(horizontal = 4.dp)
			.then(modifier),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {

		when (sheetContentType.value) {
			SheetContentType.QUEUE -> @Composable {
				item {
					Box(
						Modifier.fillMaxWidth(),
						contentAlignment = Alignment.Center
					) {
						Text(
							text = title,
							style = HeadLineMedium,
							color = MaterialTheme.colorScheme.onSurface
						)
					}
				}
				itemsIndexed(playbackState.queue) { index, track ->
					TrackSelect(track) { viewModel.playOnIndex(index) }
				}
			}

			SheetContentType.OPTIONS -> @Composable {
				val options = listOf(
					OptionValue(
						R.drawable.solar__play_bold,
						"Play Now",
					),
					OptionValue(
						R.drawable.iconoir__heart,
						"Add to Favorite",
					),
					OptionValue(
						R.drawable.hugeicons__playlist_01,
						"Add to Playlist",
					),
				)
				selectedTrack.value?.let {
					item {
						TrackSelect(
							track = selectedTrack.value!!,
							optionVisible = false
						)
						Spacer(Modifier.height(4.dp))
						HorizontalDivider(Modifier.fillMaxWidth(0.9f), color = MaterialTheme.colorScheme.onSurface.copy(0.8f))
					}
					itemsIndexed(options) { _, option ->
						OptionSelect(
							option = option,
							onClick = {}
						)
					}
				}
			}

			SheetContentType.CATEGORY_OPTION -> @Composable {
				screenData?.let {
					val options = listOfNotNull(
						OptionValue(
							painter = R.drawable.solar__play_bold,
							title = "Play all tracks",
							action = {
								viewModel.prepareAndPlay()
							}
						),
						if (it.id != 0) {
							OptionValue(
								painter = R.drawable.mdi__trash_outline,
								title = "Delete playlist ${screenData?.title}",
								action = {
									viewModel.deleteCategory(it.id)
								}
							)
						} else {
							null
						}
					)

					items(options) { option ->
						OptionSelect(option) {
							option.action()
						}
					}
				}
			}
		}

		item {
			Spacer(Modifier.height(100.dp))
		}
	}
}

@HiltViewModel
class CustomSheetViewModel @Inject constructor(
	private val nestRepository: NestRepository,
	private val mediaServiceHandler: MediaServiceHandler
) : ViewModel() {
	val playbackState = mediaServiceHandler.playbackState

	private val _screenData: MutableStateFlow<CategoryScreenData?> = MutableStateFlow(null)
	val screenData = _screenData.asStateFlow()

	private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Fetching)
	val uiState = _uiState.asStateFlow()

	fun playOnIndex(index: Int) = mediaServiceHandler.setIndex(index)

	fun prepareAndPlay(index: Int = 0) = mediaServiceHandler.setMediaItemList(screenData.value?.data?.tracks ?: emptyList(), index)

	fun fetchCategoryData(path: String) = viewModelScope.launch {
		_uiState.value = UIState.Fetching
		_screenData.value = nestRepository.getCategoryData(path)
		_uiState.value = UIState.Ready
	}

	fun deleteCategory(id: Int) = viewModelScope.launch {
		nestRepository.deletePlaylist(id)
	}
}