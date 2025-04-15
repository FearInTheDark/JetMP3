package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.ui.viewmodels.LibraryViewModel

@Composable
fun LibraryScreen(libraryViewModel: LibraryViewModel = hiltViewModel()) {

	Column(
		Modifier
			.fillMaxSize()
			.padding(4.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {

		Text(
			text = "Library Screen",
			style = HeadStyleLarge,
			color = MaterialTheme.colorScheme.onSurface
		)
		Spacer(Modifier.height(24.dp))

		Text(
			text = "No Post",
			fontFamily = FontFamily(Font(R.font.spotifymixui_bold)),
			fontWeight = FontWeight.Bold,
			letterSpacing = (-0.5).sp,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.basicMarquee()
		)

		Spacer(Modifier.height(24.dp))

		AsyncImage(
			model = "https://res.cloudinary.com/dsy29z79v/image/upload/v1744611492/cld-sample-3.jpg",
			contentDescription = null,
			modifier = Modifier
				.fillMaxWidth(0.9f)
				.clip(RoundedCornerShape(10.dp))
		)

		Spacer(Modifier.height(24.dp))

		ElevatedButton(
			onClick = { libraryViewModel.uploadResource() },
			shape = RoundedCornerShape(10.dp),
			elevation = ButtonDefaults.buttonElevation(10.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.onSurface.copy(0.9f),
				contentColor = MaterialTheme.colorScheme.surface
			)
		) {
			Text(
				text = "Upload",
				style = LabelLineMedium
			)
		}
	}

}