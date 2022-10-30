package com.strand.finaid.data.network.module

import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.data.repository.impl.CategoriesRepositoryImpl
import com.strand.finaid.data.repository.impl.SavingsRepositoryImpl
import com.strand.finaid.data.repository.impl.TransactionsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsTransactionsRepository(impl: TransactionsRepositoryImpl): TransactionsRepository

    @Binds
    fun bindsSavingsRepository(impl: SavingsRepositoryImpl): SavingsRepository

    @Binds
    fun bindsCategoriesRepository(impl: CategoriesRepositoryImpl): CategoriesRepository

}