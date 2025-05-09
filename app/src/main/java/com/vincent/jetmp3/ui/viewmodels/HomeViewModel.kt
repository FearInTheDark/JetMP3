package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.vincent.jetmp3.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val authRepository: AuthRepository
) : ViewModel() {
	init {
		// Init log
		Log.d("Initialization", "HomeViewModel initialized")
	}


	suspend fun logout() = authRepository.logout()
}