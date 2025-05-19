package com.vincent.jetmp3.data.constants

sealed class UIState {
	data object Initial : UIState()
	data object Ready : UIState()
	data object Fetching : UIState()
	data object FetchingTrack: UIState()
}