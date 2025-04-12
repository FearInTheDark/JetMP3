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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vincent.jetmp3.R

@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogin: () -> Unit = {},

) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logos__spotify_icon),
                contentDescription = "Spotify Logo",
                modifier = Modifier
                    .size(80.dp)
                    .padding(top = 40.dp, bottom = 20.dp)
            )


            Text(
                text = if (authViewModel.isLoggingIn.value) "Log in to Spotify" else "Sign up for Spotify",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
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
                onClick = { onLogin() },
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
                border = ButtonDefaults.outlinedButtonBorder().copy(
                    brush = SolidColor(Color.Gray)
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
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(Color.Gray)
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

            Spacer(modifier = Modifier.weight(1f))


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
                TextButton(onClick = { authViewModel.setIsLoggingIn(!authViewModel.isLoggingIn.value) }) {
                    Text(
                        text = if (authViewModel.isLoggingIn.value) "SIGN UP" else "LOG IN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}