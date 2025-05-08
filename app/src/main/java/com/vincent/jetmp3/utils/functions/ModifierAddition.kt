package com.vincent.jetmp3.utils.functions

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
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
