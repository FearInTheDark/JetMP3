package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HorizontalCarousel(items: List<String>) {
	LazyRow(
		modifier = Modifier
			.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {
		items(items) { item ->
			Box(
				modifier = Modifier
					.width(100.dp)
					.aspectRatio(1f)
					.clip(RoundedCornerShape(12.dp))
					.background(Color.LightGray)
					.clickable { /* Handle click */ },
				contentAlignment = Alignment.Center
			) {
				Text(text = item, fontSize = 18.sp)
			}
		}
	}
}
