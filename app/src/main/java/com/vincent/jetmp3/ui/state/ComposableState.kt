package com.vincent.jetmp3.ui.state

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import com.vincent.jetmp3.data.constants.SheetContentType
import com.vincent.jetmp3.data.models.Track

val LocalPlayingShow = compositionLocalOf<MutableState<Boolean>> {
	error("No PlayingShow state provided")
}

val LocalSelectedTrack = compositionLocalOf<MutableState<Track?>> {
	error("No SelectedTrack state provided")
}

val LocalSelectedCategory = compositionLocalOf<MutableState<String>> {
	error("No SelectedCategory state provided")
}

val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
	error("No SnackBar state provided")
}

val LocalSheetContentType = compositionLocalOf<MutableState<SheetContentType>> {
	error("No SheetContentType state provided")
}

@ExperimentalMaterial3Api
val LocalBottomSheetState = compositionLocalOf<BottomSheetScaffoldState> {
	error("No BottomSheet state provided")
}
