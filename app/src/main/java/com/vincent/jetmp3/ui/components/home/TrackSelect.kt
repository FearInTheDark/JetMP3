package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.TrackSelectType
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.data.repository.ServiceRepository
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.ui.layout.LoadingHolder
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.ui.theme.LabelLineSmall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Composable
@UnstableApi
fun TrackSelect(
	track: Track,
	boxModifier: Modifier = Modifier,
	optionVisible: Boolean = true,
	type: TrackSelectType = TrackSelectType.LIST,
	boxSize: Dp = 160.dp,
	modifier: Modifier = Modifier,
	viewModel: TrackViewModel = hiltViewModel(),
	onOptionClick: () -> Unit = {},
	onClick: () -> Unit = {},
) {
	val context = LocalContext.current
	val playbackState by viewModel.playbackState.collectAsState()

	when (type) {
		TrackSelectType.LIST -> @Composable {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 8.dp)
					.clip(RoundedCornerShape(4.dp))
					.clickable {
						onClick()
						viewModel.startService()
					}
					.then(modifier),
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
						horizontalArrangement = Arrangement.spacedBy(12.dp),
						modifier = Modifier.weight(1f)
					) {
						Box(
							modifier = Modifier
								.size(50.dp)
								.shadow(
									elevation = 4.dp
								)
						) {
							// Placeholder for album art or song icon
							AsyncImage(
								model = ImageRequest.Builder(context)
									.data(track.images.firstOrNull()).build(),
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
								fontSize = 15.sp,
								fontWeight = FontWeight.Normal,
								color = if (playbackState.currentTrack?.name == track.name) Color(0xFF1ed760) else MaterialTheme.colorScheme.onSurface,
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

					if ((playbackState.currentTrack?.id ?: -1) == track.id && playbackState.isPlaying)
						LoadingHolder(
							icon = "wave.lottie",
							modifier = Modifier.size(30.dp)
						)

					if (optionVisible)
						IconButton(
							onClick = onOptionClick
						) {
							Icon(
								imageVector = Icons.Filled.MoreVert,
								contentDescription = null,
								tint = MaterialTheme.colorScheme.onSurface
							)
						}
				}
			}
		}

		TrackSelectType.GRID -> @Composable {
			Column(
				modifier = Modifier
					.width(boxSize)
					.clip(RoundedCornerShape(8.dp))
					.clickable(onClick = onClick.also { viewModel.startService() })
					.then(boxModifier)
			) {
				Box(
					modifier = Modifier
						.size(boxSize)
						.shadow(
							elevation = 4.dp,
							shape = RoundedCornerShape(4.dp),
							clip = false
						)
				) {
					AsyncImage(
						model = ImageRequest.Builder(LocalContext.current)
							.data(track.images.first())
							.crossfade(true).build(),
						fallback = painterResource(R.drawable.material_icon_theme__gemini_ai),
						contentDescription = "Image",
						contentScale = ContentScale.Crop,
						modifier = Modifier
							.clip(RoundedCornerShape(4.dp))
							.aspectRatio(1f)
					)
				}

				// Artist name
				Text(
					text = track.name,
					style = LabelLineMedium,
					fontSize = 14.sp,
					fontWeight = FontWeight.SemiBold,
					maxLines = 1,
					letterSpacing = 0.sp,
					overflow = TextOverflow.Ellipsis,
					color = if (playbackState.currentTrack?.name == track.name) Color(0xFF1ed760) else MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.padding(top = 8.dp)
				)

				Text(
					text = track.artistType.name,
					style = LabelLineSmall,
					color = Color.Gray,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.padding(top = 4.dp)
				)
			}

		}
	}

}

@HiltViewModel
class TrackViewModel @Inject constructor(
	private val serviceRepository: ServiceRepository,
	mediaServiceHandler: MediaServiceHandler
) : ViewModel() {

	private val _track: MutableStateFlow<Track?> = MutableStateFlow(null)
	val track = _track.asStateFlow()

	val playbackState = mediaServiceHandler.playbackState

	@UnstableApi
	fun startService() = serviceRepository.startServiceRunning()

}