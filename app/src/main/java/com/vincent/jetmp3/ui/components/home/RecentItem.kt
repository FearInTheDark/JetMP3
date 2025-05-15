package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.ui.theme.LabelLineBold
import com.vincent.jetmp3.ui.theme.LabelLineSmall

@Composable
fun RecentItem(
	track: Track,
	state: UIState,
	onClick: () -> Unit = {}
) {
	Column(
		modifier = Modifier
			.width(160.dp)
			.clickable(onClick = onClick)
	) {
		Box(
			modifier = Modifier
				.size(160.dp)
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
				fallback = painterResource(R.drawable.logos__google_bard_icon),
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
			style = LabelLineBold,
			color = MaterialTheme.colorScheme.onSurface,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
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