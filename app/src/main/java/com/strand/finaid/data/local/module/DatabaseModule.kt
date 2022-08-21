package com.strand.finaid.data.local.module

import android.app.Application
import androidx.room.Room
import com.strand.finaid.data.local.FinaidDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(app, FinaidDatabase::class.java, "finaid_db_v1")
        .fallbackToDestructiveMigration()
        .build()
}