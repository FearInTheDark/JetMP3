package com.vincent.jetmp3.ui.components.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vincent.jetmp3.R

@Composable
fun PlaceholderIcon(
	icon: Int = R.drawable.iconamoon__history_bold,
	backgroundColor: Brush = Brush.linearGradient(
		colors = listOf(
			Color.Blue,
			Color.Red,
			Color.Yellow
		),
		start = Offset(0f, 0f),
		end = Offset.Infinite
	),
	modifier: Modifier = Modifier
) {
	Box(
		modifier = Modifier
			.clip(RoundedCornerShape(4.dp))
			.aspectRatio(1f)
			.background(backgroundColor)
			.padding(4.dp).then(modifier),
		contentAlignment = Alignment.Center
	) {
		Icon(
			painter = painterResource(icon),
			contentDescription = "Default Logo",
		)
	}
}
