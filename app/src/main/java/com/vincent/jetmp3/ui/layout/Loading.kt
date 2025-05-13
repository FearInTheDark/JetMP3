package com.vincent.jetmp3.ui.layout

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LoadingHolder(
	modifier: Modifier = Modifier.size(100.dp),
	icon: String = "loading.json"
) {
	val composition by rememberLottieComposition(LottieCompositionSpec.Asset(icon))
	val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
	LottieAnimation(
		composition = composition,
		progress = { progress },
		modifier = modifier
	)

}