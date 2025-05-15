package com.vincent.jetmp3.ui.components.navigation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import arrow.core.Either
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.viewmodels.NowPlayingBarViewModel
import com.vincent.jetmp3.utils.mixColors
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NowPlayingBar(
	viewModel: NowPlayingBarViewModel = hiltViewModel(),
	onClick: () -> Unit
) {
	val scope = rememberCoroutineScope()
	val context = LocalContext.current

	val playbackState by viewModel.playbackState.collectAsState()
	var dominantColor by remember { mutableStateOf(Color.Gray) }
	var offsetY by remember { mutableFloatStateOf(0f) }
	val barVisible by remember(playbackState.currentTrack) {
		mutableStateOf(playbackState.currentTrack != null)
	}

	LaunchedEffect(playbackState.currentTrack) {
		dominantColor = viewModel.getDominantColor()
	}

	val animatedColor by animateColorAsState(
		targetValue = dominantColor,
		animationSpec = tween(durationMillis = 1000, easing = { it }),
		label = "Animated NowPlayingBar Color"
	)

	AnimatedVisibility(
		visible = barVisible,
		enter = slideInVertically(
			initialOffsetY = { fullHeight -> fullHeight },
			animationSpec = tween(500),
		) + scaleIn(
			animationSpec = tween(500),
			initialScale = 0.0f
		) + fadeIn(animationSpec = tween(durationMillis = 500)),
		exit = slideOutVertically(
			targetOffsetY = { fullHeight -> fullHeight },
			animationSpec = tween(500)
		) + scaleOut(
			targetScale = 0.0f,
			animationSpec = tween(500)
		) + fadeOut(animationSpec = tween(durationMillis = 500)),
	) {
		Box(
			modifier = Modifier
				.wrapContentSize()
				.clickable { onClick() }
				.fillMaxWidth(0.95f)
				.background(
					Brush.linearGradient(
						start = Offset.Zero,
						end = Offset.Infinite,
						colors = listOf(
							mixColors(arrayOf(animatedColor to 0.8f, MaterialTheme.colorScheme.onSurface to 0.2f)),
							animatedColor,
							animatedColor,
							animatedColor,
							animatedColor,
							mixColors(arrayOf(animatedColor to 0.9f, MaterialTheme.colorScheme.onSurface to 0.1f)),
						),
						tileMode = TileMode.Clamp
					),
					RoundedCornerShape(6.dp)
				)
				.padding(6.dp)
				.pointerInput(Unit) {
					detectVerticalDragGestures(
						onVerticalDrag = { change, dragAmount ->
							change.consume()
							offsetY += dragAmount
						},
						onDragEnd = {
							if (offsetY > 100) {
								viewModel.playPause()
							} else if (offsetY < -100) {
								onClick()
							}
							offsetY = 0f
						}
					)
				},
			contentAlignment = Alignment.Center
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Row(
					modifier = Modifier.wrapContentSize(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					AsyncImage(
						model = ImageRequest.Builder(LocalContext.current).data(playbackState.currentTrack?.images?.firstOrNull()).build(),
						fallback = painterResource(R.drawable.material_icon_theme__gemini_ai),
						contentDescription = "Song Label",
						contentScale = ContentScale.Crop,
						modifier = Modifier
							.width(44.dp)
							.aspectRatio(1f)
							.clip(RoundedCornerShape(4.dp))
					)

					Column(
						verticalArrangement = Arrangement.SpaceEvenly,
						horizontalAlignment = Alignment.Start,
						modifier = Modifier
							.widthIn(max = 200.dp)
							.padding(2.dp)
					) {
						Text(
							text = playbackState.currentTrack?.name ?: "Unknown",
							fontFamily = FontFamily(Font(R.font.spotifymixui_bold)),
							color = Color.White.copy(0.9f),
							fontWeight = FontWeight.Bold,
							fontSize = 14.sp,
							letterSpacing = (-0.5).sp,
//							lineHeight = 12.sp,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
							modifier = Modifier.basicMarquee()
						)

						Spacer(Modifier.height(4.dp))

						Text(
							text = when (val artist = playbackState.currentArtist) {
								is Either.Left -> artist.value?.name ?: "Unknown Nest Artist"
								is Either.Right -> artist.value?.name ?: "Unknown Spotify Artist"
							},
							fontFamily = FontFamily(Font(R.font.spotifymixui_regular)),
							color = Color.White,
							fontSize = 12.sp,
							lineHeight = 12.sp,
							modifier = Modifier
						)
					}
				}

				Row(
					modifier = Modifier
						.wrapContentSize(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Icon(
						painter = painterResource(if (playbackState.isPlaying) R.drawable.solar__pause_bold else R.drawable.solar__play_bold),
						contentDescription = "PlayPause",
						Modifier
							.width(24.dp)
							.aspectRatio(1f)
							.clickable { viewModel.playPause() },
						tint = MaterialTheme.colorScheme.onSurface.copy(0.8f)
					)
					Icon(
						painter = painterResource(if (playbackState.currentTrack!!.isFavorite) R.drawable.iconoir__heart_solid else R.drawable.iconoir__heart),
						contentDescription = "Favorite",
						Modifier
							.width(24.dp)
							.aspectRatio(1f)
							.clickable {
								scope.launch { viewModel.toggleFavorite() }
								Toast
									.makeText(
										context,
										if (playbackState.currentTrack!!.isFavorite) "Removed from favorites" else "Added to favorites",
										Toast.LENGTH_SHORT
									)
									.show()
							},
						tint = if (playbackState.currentTrack!!.isFavorite) {
							Color(0xFFF64A55).copy(0.8f)
						} else {
							MaterialTheme.colorScheme.onSurface.copy(0.8f)
						},
					)
					Spacer(Modifier.width(8.dp))
				}
			}

			Spacer(
				Modifier
					.align(Alignment.BottomCenter)
					.height(4.dp)
			)

			LinearProgressIndicator(
				progress = { playbackState.progress / 100f },
				drawStopIndicator = {},
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.fillMaxWidth()
					.height(2.dp)
					.offset(y = 6.dp),
				color = MaterialTheme.colorScheme.onSurface,
				trackColor = MaterialTheme.colorScheme.onSurface.copy(0.2f),
			)
		}
	}
}