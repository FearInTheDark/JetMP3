package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vincent.jetmp3.ui.theme.LabelLineBold
import com.vincent.jetmp3.ui.theme.TitleLineBig
import com.vincent.jetmp3.utils.RecentCategoryItem

@Composable
fun RecentCategory(
	categories: List<RecentCategoryItem>
) {
	val columns = 2

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(4.dp),
		horizontalAlignment = Alignment.Start,
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Text(
			text = "Recent",
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
				.clip(RoundedCornerShape(4.dp))
				.background(color = MaterialTheme.colorScheme.tertiary)
			repeat(categories.size) {
				Row(
					modifier = itemModifier,
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically,
				) {
					AsyncImage(
						model = "https://i.scdn.co/image/ab67616d00001e027636e1c9e67eaafc9f49aefd",
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