package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
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

		// Request Internet Permission

		Text(
			text = "Library Screen",
			style = HeadStyleLarge,
			color = MaterialTheme.colorScheme.onSurface
		)
		Spacer(Modifier.height(24.dp))

		Text(
			text = libraryViewModel.postResponse.value?.title ?: "No Post",
			fontFamily = FontFamily(Font(R.font.spotifymixui_bold)),
			fontWeight = FontWeight.Bold,
			letterSpacing = (-0.5).sp,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.basicMarquee()
		)

		Spacer(Modifier.height(24.dp))

		OutlinedTextField(
			value = libraryViewModel.postNo.intValue.toString(),
			onValueChange = { libraryViewModel.setPostNo(it.toIntOrNull() ?: 1) },
			leadingIcon = { Icon(Icons.Outlined.NoteAlt, null) },
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Number
			)
		)

		ElevatedButton(
			onClick = { libraryViewModel.getPost() },
			shape = RoundedCornerShape(10.dp),
			elevation = ButtonDefaults.buttonElevation(10.dp),
			modifier = Modifier.padding(8.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary,

				)
		) {
			Text(
				text = "Fetch",
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}

}