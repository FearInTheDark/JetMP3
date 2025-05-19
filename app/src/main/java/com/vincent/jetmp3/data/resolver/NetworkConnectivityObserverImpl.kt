package com.vincent.jetmp3.data.resolver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.vincent.jetmp3.domain.NetworkConnectivityObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityObserverImpl @Inject constructor(
	@ApplicationContext
	private val context: Context
) : NetworkConnectivityObserver {

	private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

	private val _networkStatus: MutableStateFlow<Boolean> = MutableStateFlow(false)
	override val networkStatus = _networkStatus.asStateFlow()

	private var isRegistered = false

	private val callback = object : ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			super.onAvailable(network)
			_networkStatus.value = true
		}

		override fun onLost(network: Network) {
			super.onLost(network)
			_networkStatus.value = false
		}
	}

	init {
		CoroutineScope(Dispatchers.Default).launch {
			_networkStatus.subscriptionCount
				.collectLatest { count ->
					println("Network status subscription count: $count")
					if (count > 0 && !isRegistered) {
						start()
					} else if (count == 0 && isRegistered) {
						stop()
					}
				}
		}
	}

	override fun start() {
		if (isRegistered) return
		val request = NetworkRequest.Builder()
			.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
			.build()
		connectivityManager.registerNetworkCallback(request, callback)
	}

	override fun stop() {
		if (!isRegistered) return
		connectivityManager.unregisterNetworkCallback(callback)
	}
}