package com.vincent.jetmp3.utils.functions

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.fadingEdge(
	brush: Brush = Brush.horizontalGradient(
		0f to Color.Transparent,
		0.07f to Color.Black,
		0.93f to Color.Black,
		1f to Color.Transparent,
	)
) = this
	.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
	.drawWithContent {
		drawContent()
		drawRect(brush = brush, blendMode = BlendMode.DstIn)
	}

fun Modifier.scaleOnTap(
	scale: Float,
	onPressStart: () -> Unit,
	onPressEnd: () -> Unit,
	onTap: (() -> Unit)?
) = this
	.scale(scale)
	.pointerInput(Unit) {
		detectTapGestures(
			onPress = {
				onPressStart()
				try {
					awaitRelease()
				} finally {
					onPressEnd()
				}
			},
			onTap = { onTap?.let { onTap() } }
		)
	}

fun Modifier.shimmerBackground(shape: Shape = RectangleShape): Modifier = composed {
	val transition = rememberInfiniteTransition(label = "Transition")

	val translateAnimation by transition.animateFloat(
		initialValue = 0f,
		targetValue = 400f,
		animationSpec = infiniteRepeatable(
			tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
			RepeatMode.Restart
		),
		label = "Shimmer Animation"
	)
	val shimmerColors = listOf(
		Color.LightGray.copy(alpha = 0.9f),
		Color.LightGray.copy(alpha = 0.4f),
	)
	val brush = Brush.linearGradient(
		colors = shimmerColors,
		start = Offset(translateAnimation, translateAnimation),
		end = Offset(translateAnimation + 100f, translateAnimation + 100f),
		tileMode = TileMode.Mirror,
	)
	return@composed this.then(background(brush, shape))
}