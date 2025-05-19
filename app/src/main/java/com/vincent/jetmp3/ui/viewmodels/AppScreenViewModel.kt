package com.vincent.jetmp3.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.vincent.jetmp3.domain.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppScreenViewModel @Inject constructor(
	networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {
	val networkStatus = networkConnectivityObserver.networkStatus
}