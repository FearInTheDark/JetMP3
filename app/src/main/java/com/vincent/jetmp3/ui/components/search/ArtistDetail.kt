package com.vincent.jetmp3.ui.components.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vincent.jetmp3.domain.models.Artist
import com.vincent.jetmp3.ui.theme.LabelLineMedium

@Composable
fun ArtistDetailScreen(artist: Artist?) {
	Column(
		modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
	) {
		artist?.images?.firstOrNull()?.let { image ->
			AsyncImage(
				model = ImageRequest.Builder(LocalContext.current).data(image.url).crossfade(true)
					.build(),
				contentDescription = "Artist image",
				modifier = Modifier
					.size(200.dp)
					.clip(CircleShape),
				contentScale = ContentScale.Crop
			)
		}

		Spacer(modifier = Modifier.height(16.dp))

		Text(
			text = artist?.name ?: "Unknown Artist",
			style = LabelLineMedium,
			color = MaterialTheme.colorScheme.onSurface
		)

		Spacer(modifier = Modifier.height(8.dp))

		Text(
			text = "Popularity: ${artist?.popularity}",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface
		)

		Spacer(modifier = Modifier.height(8.dp))

		Text(
			text = "Followers: ${artist?.followers?.total}",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface
		)

		Spacer(modifier = Modifier.height(16.dp))

		if (artist?.genres?.isNotEmpty() == true) {
			Text(
				text = "Genres: ${artist.genres.joinToString(", ")}",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}