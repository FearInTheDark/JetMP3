package com.vincent.jetmp3

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.data.repository.ServiceRepository
import com.vincent.jetmp3.domain.NetworkConnectivityObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), LifecycleObserver {

	@Inject
	lateinit var networkObserver: NetworkConnectivityObserver

	@Inject
	lateinit var serviceRepository: ServiceRepository

	@UnstableApi
	override fun onTerminate() {
		super.onTerminate()
		networkObserver.stop()
		serviceRepository.stopServiceRunning()
	}
}