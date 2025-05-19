package com.vincent.jetmp3.ui.screens.test

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun FullScreenSwipeToDismiss(
	onDismissed: () -> Unit,
	content: @Composable BoxScope.() -> Unit
) {
	val offsetY = remember { Animatable(0f) }
	val scope = rememberCoroutineScope()
	val screenHeight = LocalConfiguration.current.screenHeightDp.dp

	Box(
		modifier = Modifier
			.offset { IntOffset(0, offsetY.value.toInt()) }
			.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
			.pointerInput(Unit) {
				detectVerticalDragGestures(
					onVerticalDrag = { change, dragAmount ->
						change.consume()
						if (offsetY.value + dragAmount < 0) {
							return@detectVerticalDragGestures
						}
						scope.launch {
							offsetY.snapTo(offsetY.value + dragAmount)
						}
					},
					onDragEnd = {
						scope.launch {
							if (offsetY.value > screenHeight.toPx() * 0.25f) {
								offsetY.animateTo(screenHeight.toPx(), tween(300))
								onDismissed()
							} else {
								offsetY.animateTo(0f, tween(300))
							}
						}
					}
				)
			}
	) {
		content()
	}
}
