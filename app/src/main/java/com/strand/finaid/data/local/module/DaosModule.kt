package com.strand.finaid.data.local.module

import com.strand.finaid.data.local.FinaidDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
    @Provides
    fun provideTransactionsDao(database: FinaidDatabase) = database.transactionsDao()

    @Provides
    fun provideSavingsDao(database: FinaidDatabase) = database.savingsDao()
}