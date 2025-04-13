package com.vincent.jetmp3.ui.viewmodels

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.domain.ApiService
import com.vincent.jetmp3.domain.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
	private val apiService: ApiService
) : ViewModel() {
	private var _postNo = mutableIntStateOf(1)
	val postNo = _postNo

	private var _postResponse = mutableStateOf<Post?>(null)
	val postResponse = _postResponse

	fun setPostNo(postNo: Int) {
		_postNo.intValue = postNo
	}

	// This function is called when the post number is changed



	fun getPost() {
		viewModelScope.launch {
			_postResponse.value = apiService.getPost(postNo.intValue).body()

		}
	}

}