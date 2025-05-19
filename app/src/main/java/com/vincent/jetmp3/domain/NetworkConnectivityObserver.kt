package com.vincent.jetmp3.domain

import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectivityObserver {
	val networkStatus: StateFlow<Boolean>
	fun start()
	fun stop()
}