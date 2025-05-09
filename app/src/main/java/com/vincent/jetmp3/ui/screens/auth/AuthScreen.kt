package com.vincent.jetmp3.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.layout.LoadingOverlay
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineBold
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.ui.theme.TitleLineBig
import com.vincent.jetmp3.ui.theme.TitleLineLarge
import com.vincent.jetmp3.ui.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
	authViewModel: AuthViewModel = hiltViewModel(),
	onValidated: () -> Unit = {},
) {
	val uiState by authViewModel.uiState.collectAsState()
	val errorMessages by authViewModel.errorMessage.collectAsState()

	val shouldBlur by remember(errorMessages, uiState) {
		derivedStateOf { errorMessages.isNotEmpty() || uiState == AuthViewModel.AuthState.Fetching }
	}

	LaunchedEffect(Unit) {
		authViewModel.authValid.collect { isValid ->
			if (isValid) onValidated()
		}
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					colors = listOf(
						Color(0xFF1DB954).copy(0.8f),
						Color(0xFF1DB954).copy(0.4f),
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
					),
					tileMode = TileMode.Clamp,
				)
			)
			.blur(if (shouldBlur) 8.dp else 0.dp)
			.padding(horizontal = 24.dp)
	) {
		Column(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {

			Image(
				painter = painterResource(id = R.drawable.material_icon_theme__gemini_ai),
				contentDescription = "Spotify Logo",
				modifier = Modifier
					.size(160.dp)
					.padding(top = 40.dp, bottom = 20.dp)
			)


			AnimatedContent(
				targetState = authViewModel.isLoggingIn.value,
				label = "AuthTitle",
				transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(300)) },
			) { isLoggingIn ->
				Text(
					text = if (isLoggingIn) "Log in to ${stringResource(R.string.app_name)}"
					else "Sign up for ${stringResource(R.string.app_name)}",
					style = HeadStyleLarge,
					color = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.padding(bottom = 24.dp)
				)
			}


			OutlinedTextField(
				value = authViewModel.email.value,
				onValueChange = { authViewModel.email.value = it },
				label = { Text("Email") },
				textStyle = TitleLineBig,
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(8.dp),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Email
				),
				singleLine = true
			)


			AnimatedVisibility(
				visible = !authViewModel.isLoggingIn.value,
				enter = fadeIn(
					animationSpec = tween(500)
				) + expandVertically(),
				exit = fadeOut(animationSpec = tween(500)) + shrinkVertically()
			) {
				OutlinedTextField(
					value = authViewModel.username.value,
					onValueChange = { authViewModel.username.value = it },
					label = { Text("What should we call you?") },
					textStyle = TitleLineBig,
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(8.dp),
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Text
					),
					singleLine = true,
				)
			}


			OutlinedTextField(
				value = authViewModel.password.value,
				onValueChange = { authViewModel.password.value = it },
				label = { Text("Password") },
				textStyle = TitleLineBig,
				visualTransformation = PasswordVisualTransformation(),
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(8.dp),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Password
				),
				singleLine = true
			)


			if (authViewModel.isLoggingIn.value) {
				TextButton(
					onClick = { /* TODO: Implement forgot password */ },
					modifier = Modifier.align(Alignment.End)
				) {
					Text(
						text = "Forgot password?",
						color = MaterialTheme.colorScheme.onSurface,
						fontSize = 14.sp,
						style = LabelLineMedium
					)
				}
			}

			Spacer(modifier = Modifier.height(8.dp))


			Button(
				onClick = { authViewModel.handle() },
				modifier = Modifier
					.fillMaxWidth()
					.height(50.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = Color(0xFF1DB954)
				),
				shape = RoundedCornerShape(50)
			) {
				Text(
					text = if (authViewModel.isLoggingIn.value) "LOG IN" else "SIGN UP",
					style = TitleLineLarge,
					fontSize = 16.sp,
				)
			}


			Text(
				text = "Or continue with",
				color = Color.Gray,
				style = LabelLineMedium,
				fontSize = 14.sp,
				modifier = Modifier.padding(vertical = 16.dp)
			)


			OutlinedButton(
				onClick = { /* TODO: Implement Google login */ },
				modifier = Modifier
					.fillMaxWidth()
					.height(50.dp),
				border = ButtonDefaults.outlinedButtonBorder(
					enabled = true
				),
				shape = RoundedCornerShape(50)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center
				) {
					Image(
						painter = painterResource(id = R.drawable.logos__google_icon),
						contentDescription = "Google Logo",
						modifier = Modifier.size(24.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = "CONTINUE WITH GOOGLE",
						style = TitleLineLarge,
						fontSize = 14.sp,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}


			OutlinedButton(
				onClick = { /* TODO: Implement Facebook login */ },
				modifier = Modifier
					.fillMaxWidth()
					.height(50.dp),
				border = ButtonDefaults.outlinedButtonBorder(
					enabled = true
				),
				shape = RoundedCornerShape(50)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center
				) {
					Image(
						painter = painterResource(id = R.drawable.logos__facebook),
						contentDescription = "Facebook Logo",
						modifier = Modifier.size(24.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = "CONTINUE WITH FACEBOOK",
						fontSize = 14.sp,
						style = TitleLineLarge,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}


			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = if (authViewModel.isLoggingIn.value) "Don't have an account?" else "Already have an account?",
					color = Color.Gray,
					style = LabelLineMedium,
					fontSize = 14.sp
				)
				TextButton(onClick = { authViewModel.isLoggingIn.value = !authViewModel.isLoggingIn.value }) {
					Text(
						text = if (authViewModel.isLoggingIn.value) "SIGN UP" else "LOG IN",
						style = LabelLineBold,
						color = MaterialTheme.colorScheme.onSurface,
						fontSize = 14.sp
					)
				}
			}
		}
	}
	if (errorMessages.isNotEmpty()) {
		Dialog(onDismissRequest = { authViewModel.clearErrors() }) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
					.padding(16.dp),
				contentAlignment = Alignment.Center
			) {
				Column(
					modifier = Modifier.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {

					Text(
						text = "Error",
						style = TitleLineLarge,
						color = MaterialTheme.colorScheme.onSurface,
						fontSize = 18.sp
					)
					Spacer(modifier = Modifier.height(8.dp))

					errorMessages.forEach {
						Text(
							// Big dot character + it
							text = "\u2022 $it",
							style = LabelLineMedium,
							color = MaterialTheme.colorScheme.onSurface,
							fontSize = 16.sp
						)

					}
				}
			}
		}
	}
	LoadingOverlay(isLoading = uiState == AuthViewModel.AuthState.Fetching)
}