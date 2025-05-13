package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.data.models.NestArtist
import com.vincent.jetmp3.ui.theme.LabelLineMedium

@Composable
fun ArtistSearchSelect(
	artist: NestArtist,
	onClick: () -> Unit = {}
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp)
			.clickable { onClick() }
	) {
		Row(
			Modifier
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
						model = artist.images.firstOrNull() ?: "https://i.scdn.co/image/ab6761670000ecd445c7565ba8453d2710c9c1b8",
						fallback = painterResource(R.drawable.mdi__artist),
						contentDescription = null,
						modifier = Modifier
							.clip(CircleShape)
							.align(Alignment.Center),
						contentScale = ContentScale.Crop,
					)
				}

				Column(
					verticalArrangement = Arrangement.spacedBy(2.dp),
					modifier = Modifier.widthIn(max = 220.dp)
				) {
					Text(
						text = artist.name,
						style = LabelLineMedium,
						fontWeight = FontWeight.Normal,
						color = MaterialTheme.colorScheme.onSurface,
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
					painter = painterResource(R.drawable.mdi__artist),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurface
				)
			}

		}
	}
}