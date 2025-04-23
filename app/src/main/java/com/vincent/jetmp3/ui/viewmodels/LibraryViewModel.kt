package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.domain.ImagePaletteService
import com.vincent.jetmp3.domain.models.request.VibrantRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
	private val imagePaletteService: ImagePaletteService
) : ViewModel() {

	fun getPalette() {
		viewModelScope.launch {
			val palette = imagePaletteService
				.getPalette(VibrantRequest("https://res.cloudinary.com/dsy29z79v/image/upload/v1744611492/cld-sample-3.jpg"))
			Log.d("Palette", "Palette: $palette")
		}
	}

}