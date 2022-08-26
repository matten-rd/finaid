package com.strand.finaid.data.network.module

import com.strand.finaid.data.network.CategoriesNetworkDataSource
import com.strand.finaid.data.network.SavingsNetworkDataSource
import com.strand.finaid.data.network.TransactionsNetworkDataSource
import com.strand.finaid.data.network.impl.CategoriesNetworkDataSourceImpl
import com.strand.finaid.data.network.impl.SavingsNetworkDataSourceImpl
import com.strand.finaid.data.network.impl.TransactionsNetworkDataSourceImpl
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
    fun bindsTransactionsNetworkDataSource(impl: TransactionsNetworkDataSourceImpl): TransactionsNetworkDataSource

    @Binds
    fun bindsSavingsRepository(impl: SavingsRepositoryImpl): SavingsRepository

    @Binds
    fun bindsSavingsNetworkDataSource(impl: SavingsNetworkDataSourceImpl): SavingsNetworkDataSource

    @Binds
    fun bindsCategoriesRepository(impl: CategoriesRepositoryImpl): CategoriesRepository

    @Binds
    fun bindsCategoriesNetworkDataSource(impl: CategoriesNetworkDataSourceImpl): CategoriesNetworkDataSource

}