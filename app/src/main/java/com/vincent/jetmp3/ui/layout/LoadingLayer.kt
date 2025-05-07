package com.vincent.jetmp3.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LoadingOverlay(
	isLoading: Boolean,
	modifier: Modifier = Modifier,
	backgroundColor: Color = Color.Black.copy(0.5f),
	icon: String = "loading.json",
) {
	val composition by rememberLottieComposition(LottieCompositionSpec.Asset(icon))
	val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
	if (isLoading) {
		Box(
			modifier = modifier
				.fillMaxSize()
				.background(backgroundColor)
				.zIndex(1f),
			contentAlignment = Alignment.Center
		) {
			LottieAnimation(
				composition = composition,
				progress = { progress },
				modifier = Modifier.size(100.dp)
			)
		}
	}
}
