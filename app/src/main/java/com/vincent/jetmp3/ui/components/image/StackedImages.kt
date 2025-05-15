package com.vincent.jetmp3.ui.components.image

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage

@Composable
fun StackedImages(
	imageUrls: List<String>,
	imageSize: Dp = 200.dp,
	offsetStep: Dp = 18.dp,
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier
			.size(imageSize + offsetStep * (imageUrls.size.coerceAtMost(4) - 1))
			,
	) {
		imageUrls.take(4).reversed().forEachIndexed { index, url ->
			Card(
				modifier = Modifier
					.zIndex(index.toFloat())
					.size(imageSize)
					.offset(
						x = offsetStep * index,
						y = (offsetStep / 1.5f) * index
					),
				shape = RoundedCornerShape(8.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
			) {
				AsyncImage(
					model = url,
					contentDescription = "Stacked image ${index + 1}",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.fillMaxSize()
						.aspectRatio(1f)
				)
			}
		}
	}
}