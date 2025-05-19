package com.vincent.jetmp3.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.ui.theme.TitleLineBig
import com.vincent.jetmp3.ui.theme.TitleLineLarge
import com.vincent.jetmp3.ui.viewmodels.AuthViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail

@Composable
fun OtpVerificationScreen(
	viewModel: AuthViewModel,
	onCancelClick: () -> Unit
) {

	val forgotStep by viewModel.forgotStep
	var forgotEmail by viewModel.forgotEmail
	var otpValue by viewModel.otp
	var newPassword by viewModel.newPassword

	BackHandler { onCancelClick() }

	LaunchedEffect(forgotStep) {
		if (forgotStep == 4) onCancelClick()
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					colors = listOf(
						Color(0xFF1DB954),
						Color(0xFF1DB954),
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
						MaterialTheme.colorScheme.surface,
					)
				)
			)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Icon(
				painter = painterResource(id = R.drawable.material_icon_theme__gemini_ai),
				contentDescription = "Spotify Logo",
				modifier = Modifier
					.size(160.dp)
					.padding(top = 20.dp, bottom = 20.dp)
			)

			Text(
				text = "Verify Your Account",
				style = HeadStyleLarge,
				color = Color.White
			)

			Text(
				text = when (forgotStep) {
					1 -> "Enter your email to receive a verification code"
					2 -> "Enter the verification code sent to your email"
					3 -> "Enter your new password"
					else -> "Verify Your Account"
				},
				fontSize = 16.sp,
				color = Color.White.copy(alpha = 0.8f),
				textAlign = TextAlign.Center
			)

			Spacer(modifier = Modifier.height(12.dp))

			if (forgotStep >= 1)
				OutlinedTextField(
					value = forgotEmail,
					onValueChange = { forgotEmail = it },
					label = { Text("Enter email", style = LabelLineMedium) },
					textStyle = TitleLineBig,
					leadingIcon = { Icon(
						imageVector = Icons.Outlined.Mail,
						contentDescription = "Lock"
					)},
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(8.dp),
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Email
					),
					colors = TextFieldDefaults.colors(
						focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
						focusedTextColor = MaterialTheme.colorScheme.onSurface,
						focusedLabelColor = MaterialTheme.colorScheme.onSurface,
						focusedContainerColor = Color.Transparent,
						unfocusedContainerColor = Color.Transparent,
					),
					singleLine = true,
					enabled = forgotStep == 1,
				)

			if (forgotStep >= 2) {
				Spacer(Modifier.height(8.dp))
				OtpInputField(
					enabled = forgotStep == 2,
					otpValue = otpValue,
					onOtpValueChange = { index, value ->
						otpValue = otpValue.toMutableList().apply {
							this[index] = value
						}
					}
				)
			}

			if (forgotStep >= 3)
				OutlinedTextField(
					value = newPassword,
					onValueChange = { newPassword = it },
					label = { Text("New Password", style = LabelLineMedium) },
					textStyle = TitleLineBig,
					leadingIcon = { Icon(
						imageVector = Icons.Outlined.Lock,
						contentDescription = "Lock"
					)},
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(8.dp),
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Text
					),
					singleLine = true,
					enabled = forgotStep == 3,
				)


			Spacer(modifier = Modifier.weight(1f))

			Column(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Button(
					onClick = { viewModel.handleForgot() },
					modifier = Modifier
						.fillMaxWidth()
						.height(50.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFF1DB954)
					),
					shape = RoundedCornerShape(28.dp)
				) {
					Text(
						text = when (forgotStep) {
							1 -> "SEND CODE"
							2 -> "VERIFY"
							3 -> "RESET PASSWORD"
							else -> "SEND CODE"
						},
						style = TitleLineLarge,
						fontSize = 16.sp,
					)
				}

				OutlinedButton(
					onClick = onCancelClick,
					modifier = Modifier
						.fillMaxWidth()
						.height(50.dp),
					colors = ButtonDefaults.outlinedButtonColors(
						contentColor = Color.White
					),
					border = BorderStroke(1.dp, Color.White),
					shape = RoundedCornerShape(28.dp)
				) {
					Text(
						text = "CANCEL",
						style = TitleLineLarge,
						fontSize = 16.sp,
					)
				}
			}

			if (forgotStep == 2)
				TextButton(
					onClick = { },
					modifier = Modifier.padding(bottom = 16.dp)
				) {
					Text(
						text = "Didn't receive a code? Resend",
						color = Color.White,
						fontSize = 16.sp
					)
				}
		}
	}
}

@Composable
fun OtpInputField(
	enabled: Boolean = true,
	otpValue: List<String>,
	onOtpValueChange: (Int, String) -> Unit
) {
	val focusRequesters = List(6) { FocusRequester() }

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		repeat(6) { index ->
			OutlinedTextField(
				value = otpValue[index],
				onValueChange = { value ->
					if (value.length <= 1) {
						onOtpValueChange(index, value)
						if (value.isNotEmpty() && index < 5) {
							focusRequesters[index + 1].requestFocus()
						}
					}
				},
				modifier = Modifier
					.weight(1f)
					.size(56.dp)
					.focusRequester(focusRequesters[index])
					.onKeyEvent {
						if (it.key == Key.Backspace) {
							if (otpValue[index].isEmpty() && index > 0) {
								focusRequesters[index - 1].requestFocus()
								onOtpValueChange(index - 1, "")
							} else {
								onOtpValueChange(index, "")
							}
						}
						true
					},
				textStyle = LocalTextStyle.current.copy(
					textAlign = TextAlign.Center,
					fontWeight = FontWeight.Bold,
					fontSize = 20.sp,
				),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				singleLine = true,
				maxLines = 1,
				colors = OutlinedTextFieldDefaults.colors(
					unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
					focusedBorderColor = Color.White,
					unfocusedContainerColor = Color.Transparent,
					focusedContainerColor = Color.White.copy(alpha = 0.1f)
				),
				enabled = enabled
			)
		}
	}

	LaunchedEffect(Unit) {
		focusRequesters[otpValue.indexOfFirst { it.isEmpty() }].requestFocus()
	}
}