package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.repository.NestRepository
import com.vincent.jetmp3.domain.models.RecentCategoryItem
import com.vincent.jetmp3.ui.components.image.PlaceholderIcon
import com.vincent.jetmp3.ui.state.LocalSelectedCategory
import com.vincent.jetmp3.ui.theme.LabelLineBold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun RecentCategory(
	viewModel: RecentCategoryViewModel = hiltViewModel(),
	action: () -> Unit = {}
) {
	val columns = 2
	val selectedCategory = LocalSelectedCategory.current
	val categories by viewModel.categories.collectAsState()

	LaunchedEffect(Unit) {
		viewModel.fetch()
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(
				horizontal = 8.dp,
				vertical = 4.dp
			),
		horizontalAlignment = Alignment.Start,
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		categories?.let {
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(2.dp),
				maxItemsInEachRow = columns
			) {
				val itemModifier = Modifier
					.height(60.dp)
					.padding(4.dp)
					.weight(1f)
					.shadow(
						elevation = 4.dp,
						shape = RoundedCornerShape(4.dp),
						clip = false
					)
					.clip(RoundedCornerShape(4.dp))
					.background(color = MaterialTheme.colorScheme.tertiary)

				categories?.favorite?.let {
					Row(
						modifier = itemModifier.clickable { selectedCategory.value = it.url; action() },
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						AsyncImage(
							model = categories?.favorite?.iconUri,
							contentDescription = "Favorite",
							contentScale = ContentScale.Crop,
							modifier = Modifier
								.clip(RoundedCornerShape(4.dp))
								.aspectRatio(1f)
						)

						Text(
							text = categories?.favorite?.title ?: "",
							style = LabelLineBold,
							fontSize = 14.sp,
							color = MaterialTheme.colorScheme.onSurface
						)
					}
				}

				categories?.history?.let {
					Row(
						modifier = itemModifier.clickable { selectedCategory.value = it.url; action() },
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						if (it.iconUri == null) {
							PlaceholderIcon()
						} else {
							AsyncImage(
								model = it.iconUri,
								contentDescription = "Favorite",
								contentScale = ContentScale.Crop,
								modifier = Modifier
									.clip(RoundedCornerShape(4.dp))
									.aspectRatio(1f)
							)
						}

						Text(
							text = categories?.history?.title ?: "",
							style = LabelLineBold,
							fontSize = 14.sp,
							color = MaterialTheme.colorScheme.onSurface
						)
					}
				}

				if (categories!!.playlists.isNotEmpty()) {
					categories!!.playlists.forEach {
						Row(
							modifier = itemModifier.clickable { selectedCategory.value = it.url; action() },
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalAlignment = Alignment.CenterVertically,
						) {
							if (it.iconUri == null) {
								PlaceholderIcon(
									icon = R.drawable.hugeicons__playlist_01,
									backgroundColor = Brush.linearGradient(
										colors = listOf(
											Color.Blue,
											Color.Yellow
										),
										start = Offset(0f, 0f),
										end = Offset.Infinite
									)
								)
							} else {
								AsyncImage(
									model = it.iconUri,
									contentDescription = "Favorite",
									contentScale = ContentScale.Crop,
									modifier = Modifier
										.clip(RoundedCornerShape(4.dp))
										.aspectRatio(1f)
								)
							}

							Text(
								text = it.title,
								style = LabelLineBold,
								fontSize = 14.sp,
								color = MaterialTheme.colorScheme.onSurface
							)
						}

					}
				}
			}
		}
	}
}

@HiltViewModel
class RecentCategoryViewModel @Inject constructor(
	private val nestRepository: NestRepository
) : ViewModel() {

	private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Fetching)

	private val _categories: MutableStateFlow<RecentCategoryItem?> = MutableStateFlow(null)
	val categories = _categories.asStateFlow()

	fun fetch() {
		viewModelScope.launch {
			_uiState.value = UIState.Fetching
			_categories.value = nestRepository.getRecentCategories()
			_uiState.value = UIState.Ready
		}
	}
}