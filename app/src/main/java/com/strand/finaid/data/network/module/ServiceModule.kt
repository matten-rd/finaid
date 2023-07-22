package com.strand.finaid.data.network.module

import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.network.impl.LogServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ServiceModule {

    @Provides
    fun provideLogService(impl: LogServiceImpl): LogService = impl

}