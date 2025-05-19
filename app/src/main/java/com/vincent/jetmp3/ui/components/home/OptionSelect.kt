package com.vincent.jetmp3.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.utils.OptionValue

@Composable
fun OptionSelect(
	option: OptionValue,
	onClick: () -> Unit = {}
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp)
			.clip(RoundedCornerShape(4.dp))
			.clickable { onClick() }
	) {
		Row(
			Modifier
				.fillMaxWidth()
				.padding( horizontal = 4.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Box(
				Modifier.size(50.dp),
			) {
				Icon(
					painter = painterResource(option.painter),
					contentDescription = option.title,
					tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
					modifier = Modifier.size(30.dp).align(Alignment.Center)
				)
			}

			Text(
				text = option.title,
				style = LabelLineMedium,
				fontSize = 15.sp,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
			)
		}
	}
}