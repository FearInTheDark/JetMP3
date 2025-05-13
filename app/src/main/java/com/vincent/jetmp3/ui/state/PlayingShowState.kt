package com.vincent.jetmp3.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf

val LocalPlayingShow = compositionLocalOf<MutableState<Boolean>> {
	error("No PlayingShow state provided")
}
