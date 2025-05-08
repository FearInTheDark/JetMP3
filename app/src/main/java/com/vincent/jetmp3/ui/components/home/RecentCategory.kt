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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.ui.theme.LabelLineBold
import com.vincent.jetmp3.ui.theme.TitleLineBig
import com.vincent.jetmp3.utils.RecentCategoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun RecentCategory(
	categories: List<RecentCategoryItem>,
	recentCategoryViewModel: RecentCategoryViewModel = hiltViewModel()
) {
	val columns = 2

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
		Text(
			text = "Categories",
			style = TitleLineBig,
			fontSize = 24.sp,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.padding(horizontal = 4.dp),
			letterSpacing = -(0.5).sp
		)

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
				.clickable { recentCategoryViewModel.handle() }
			repeat(categories.size) {
				Row(
					modifier = itemModifier,
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically,
				) {
					AsyncImage(
						model = ImageRequest.Builder(LocalContext.current)
							.data("https://i.scdn.co/image/ab67616d00001e027636e1c9e67eaafc9f49aefd")
							.crossfade(true).build(),
						contentDescription = "Image",
						contentScale = ContentScale.Crop,
						modifier = Modifier
							.clip(RoundedCornerShape(4.dp))
							.aspectRatio(1f)
					)

					Text(
						text = "Manic",
						style = LabelLineBold,
						fontSize = 14.sp,
						color = MaterialTheme.colorScheme.onSurface
					)
				}
			}
		}
	}
}

@HiltViewModel
class RecentCategoryViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler,

) : ViewModel() {




	fun handle() {
		mediaServiceHandler.setMediaItem(
			MediaItem.fromUri("https://res.cloudinary.com/dsy29z79v/video/upload/v1746640209/XGetter_-L%E1%BB%87_L%C6%B0u_Ly_x_Em_M%C3%A2y_-_Huy_PT_Remix_leluuly_huyptremix_nhachaymoinga-20250507174857_lmbttw.mp3")
		)
	}
}