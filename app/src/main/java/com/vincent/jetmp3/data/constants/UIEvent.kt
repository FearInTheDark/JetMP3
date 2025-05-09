package com.vincent.jetmp3.data.constants

sealed class UIEvent {
	data object Forward : UIEvent()
	data object Backward : UIEvent()
	data object PlayPause : UIEvent()
	data object SeekToNext : UIEvent()
	data object SeekToPrevious : UIEvent()
	data object FetchAudio : UIEvent()
	data class SeekTo(val position: Float) : UIEvent()
	data class UpdateProgress(val progress: Float) : UIEvent()
	data class SelectedAudioChange(val index: Int) : UIEvent()
}
