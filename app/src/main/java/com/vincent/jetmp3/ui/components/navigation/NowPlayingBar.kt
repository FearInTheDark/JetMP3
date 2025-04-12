package com.vincent.jetmp3.ui.components.navigation

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.viewmodels.NowPlayingBarViewModel

@Composable
fun NowPlayingBar(
	nowPlayingBarViewModel: NowPlayingBarViewModel = hiltViewModel<NowPlayingBarViewModel>(),
	onClick: () -> Unit
) {
	var visible by remember { mutableStateOf(false) }
	val currentSong by nowPlayingBarViewModel.currentSong.collectAsState()
	val progress by nowPlayingBarViewModel.progress.collectAsState()

	val progressFloat = remember(currentSong, progress) {
		val duration = currentSong?.duration?.takeIf { it > 0 } ?: return@remember 0f
		(progress.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
	}

	LaunchedEffect(Unit) {
		visible = true
	}

	AnimatedVisibility(
		visible = visible,
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
				.background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(6.dp))
				.padding(6.dp),
			contentAlignment = Alignment.Center
		)
		{
			Row(
				modifier = Modifier
					.fillMaxWidth(),
//				.border(1.dp, Color.Blue),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Row(
					modifier = Modifier
						.wrapContentSize(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Icon(
						painter = painterResource(R.drawable.material_icon_theme__gemini_ai),
						contentDescription = "Spotify",
						Modifier
							.width(44.dp)
							.aspectRatio(1f),
						tint = Color.Unspecified
					)

					Column(
						verticalArrangement = Arrangement.SpaceEvenly,
						horizontalAlignment = Alignment.Start,
						modifier = Modifier.widthIn(max = 200.dp)
					) {
						Text(
							text = currentSong?.title ?: "None",
							fontFamily = FontFamily(Font(R.font.spotifymixui_bold)),
							color = MaterialTheme.colorScheme.onSurface,
							fontWeight = FontWeight.Bold,
							fontSize = 14.sp,
							letterSpacing = TextUnit(0.7f, TextUnitType.Sp),
							overflow = TextOverflow.Ellipsis,
							modifier = Modifier.basicMarquee()
						)

						Text(
							text = currentSong?.artist ?: "No artist",
							fontFamily = FontFamily(Font(R.font.spotifymixui_regular)),
							color = MaterialTheme.colorScheme.onSurface,
							fontSize = 12.sp
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
						painter = painterResource(R.drawable.logos__google_bard_icon),
						contentDescription = "Spotify",
						Modifier
							.width(44.dp)
							.aspectRatio(1f),
						tint = Color.Unspecified
					)

				}
			}

			Spacer(
				Modifier
					.align(Alignment.BottomCenter)
					.height(4.dp)
			)

			LinearProgressIndicator(
				progress = { progressFloat },
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.fillMaxWidth()
					.height(2.dp)
					.offset(y = 6.dp),
			)
		}
	}
}