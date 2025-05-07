package com.vincent.jetmp3.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.TitleLineLarge
import com.vincent.jetmp3.ui.viewmodels.AuthViewModel

@Composable
fun AuthWelcome(
	onSignUpAction: () -> Unit = {},
	onSignInAction: () -> Unit = {},
	onValidated: () -> Unit,
	authViewModel: AuthViewModel = hiltViewModel()
) {
	LaunchedEffect(Unit) {
		authViewModel.authValid.collect { isValid ->
			if (isValid) onValidated()
		}
	}

	Box(
		Modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					colors = listOf(
						Color.Gray,
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
					)
				)
			)
			.padding(horizontal = 18.dp, vertical = 50.dp),
		contentAlignment = Alignment.Center,
	) {
		Column(
			modifier = Modifier.fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Icon(
				painter = painterResource(R.drawable.material_icon_theme__gemini_ai),
				contentDescription = "Welcome",
				tint = Color.Unspecified,
				modifier = Modifier.size(100.dp)
			)

			Text(
				text = stringResource(R.string.auth_welcome),
				style = HeadStyleLarge,
				fontSize = 36.sp,
				color = MaterialTheme.colorScheme.onSurface,
				textAlign = TextAlign.Center
			)

			Spacer(Modifier.height(48.dp))
		}

		Column(
			modifier = Modifier.align(Alignment.BottomCenter),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Button(
				onClick = { onSignUpAction() },
				modifier = Modifier
					.fillMaxWidth(0.9f)
					.height(50.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = Color(0xFF1DB954)
				),
				shape = RoundedCornerShape(50)
			) {
				Text(
					text = stringResource(R.string.auth_signup),
					style = TitleLineLarge,
					fontSize = 16.sp,
				)
			}
			Button(
				onClick = { onSignInAction() },
				modifier = Modifier
					.fillMaxWidth(0.9f)
					.height(50.dp)
					.border((1.5).dp, Color.Gray, RoundedCornerShape(50.dp)),
				colors = ButtonDefaults.buttonColors(
					containerColor = Color.Transparent,
				),
				shape = RoundedCornerShape(50)
			) {
				Text(
					text = stringResource(R.string.auth_signin),
					style = TitleLineLarge,
					fontSize = 16.sp,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}

		}
	}
}