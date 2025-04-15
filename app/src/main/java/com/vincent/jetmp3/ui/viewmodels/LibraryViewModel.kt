package com.vincent.jetmp3.ui.viewmodels

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.utils.ObjectUtils
import com.vincent.jetmp3.R
import com.vincent.jetmp3.core.config.CloudinaryConfig
import com.vincent.jetmp3.domain.ApiService
import com.vincent.jetmp3.domain.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
	private val apiService: ApiService
) : ViewModel() {

	fun uploadResource() {
		viewModelScope.launch {
			CloudinaryConfig.uploadImage(File("/storage/emulated/0/Download/spider.jpg")
				// File path
//				"https://fastly.picsum.photos/id/372/536/354.jpg?hmac=WzFt75PCZZoYQOknk5yWvMfGg9HW94Z7GcOX6lRxAUQ"
			)
		}
	}

}