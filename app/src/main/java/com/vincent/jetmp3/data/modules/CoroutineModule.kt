package com.vincent.jetmp3.data.modules

import com.vincent.jetmp3.core.annotation.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoroutineModule {

	@Provides
	@Singleton
	@ApplicationScope
	fun provideApplicationScope(): CoroutineScope {
		return CoroutineScope(SupervisorJob() + Dispatchers.IO)
	}

}