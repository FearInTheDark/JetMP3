package com.vincent.jetmp3.ui.screens.auth

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
	authViewModel: AuthViewModel = hiltViewModel(),
	onValidated: () -> Unit = {},
) {
	val uiState by authViewModel.uiState.collectAsState()

	LaunchedEffect(authViewModel.authValid) {
		authViewModel.authValid.collect { isValid ->
			if (isValid) onValidated()
		}
	}

	when (uiState) {
		AuthViewModel.AuthState.Fetching -> @Composable {
			Column(
				Modifier
					.fillMaxSize()
					.padding(4.dp),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Error",
					style = HeadStyleLarge,
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		}

		AuthViewModel.AuthState.Ready -> @Composable {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(MaterialTheme.colorScheme.surface)
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


					Text(
						text = if (authViewModel.isLoggingIn.value)
							"Log in to ${stringResource(R.string.app_name)}"
						else "Sign up for ${stringResource(R.string.app_name)}",
						style = HeadStyleLarge,
						color = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(bottom = 24.dp)
					)


					OutlinedTextField(
						value = authViewModel.email.value,
						onValueChange = { authViewModel.email.value = it },
						label = { Text("Email or username") },
						modifier = Modifier.fillMaxWidth(),
						shape = RoundedCornerShape(4.dp),
						singleLine = true
					)


					if (!authViewModel.isLoggingIn.value) {
						OutlinedTextField(
							value = authViewModel.username.value,
							onValueChange = { authViewModel.username.value = it },
							label = { Text("What should we call you?") },
							modifier = Modifier.fillMaxWidth(),
							shape = RoundedCornerShape(4.dp),
							singleLine = true
						)
					}


					OutlinedTextField(
						value = authViewModel.password.value,
						onValueChange = { authViewModel.password.value = it },
						label = { Text("Password") },
						visualTransformation = PasswordVisualTransformation(),
						modifier = Modifier.fillMaxWidth(),
						shape = RoundedCornerShape(4.dp),
						singleLine = true
					)


					if (authViewModel.isLoggingIn.value) {
						TextButton(
							onClick = { /* TODO: Implement forgot password */ },
							modifier = Modifier.align(Alignment.End)
						) {
							Text(
								text = "Forgot password?",
								color = Color.White,
								fontSize = 14.sp
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
							fontSize = 16.sp,
							fontWeight = FontWeight.Bold
						)
					}


					Text(
						text = "Or continue with",
						color = Color.Gray,
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
								fontSize = 14.sp,
								fontWeight = FontWeight.Bold
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
								fontWeight = FontWeight.Bold
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
							fontSize = 14.sp
						)
						TextButton(onClick = { authViewModel.isLoggingIn.value.not() }) {
							Text(
								text = if (authViewModel.isLoggingIn.value) "SIGN UP" else "LOG IN",
								color = MaterialTheme.colorScheme.onSurface,
								fontWeight = FontWeight.Bold,
								fontSize = 14.sp
							)
						}
					}
				}
			}
		}
	}

}