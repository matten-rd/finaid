package com.strand.finaid.ui.theme.settings

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserSettingsModule {
    @Binds
    @Singleton
    abstract fun bindUserSettings(impl: UserSettingsImpl): UserSettings
}