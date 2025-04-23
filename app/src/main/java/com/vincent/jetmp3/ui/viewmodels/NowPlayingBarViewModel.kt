package com.vincent.jetmp3.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.vincent.jetmp3.media.service.MediaServiceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingBarViewModel @Inject constructor(
	private val mediaServiceHandler: MediaServiceHandler
) : ViewModel() {

}