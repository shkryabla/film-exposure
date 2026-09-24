package com.filmexposure.di

import com.filmexposure.data.repository.FormatRepositoryImpl
import com.filmexposure.data.repository.ProfileRepositoryImpl
import com.filmexposure.data.repository.SettingsRepositoryImpl
import com.filmexposure.domain.repository.FormatRepository
import com.filmexposure.domain.repository.ProfileRepository
import com.filmexposure.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    abstract fun bindFormatRepository(impl: FormatRepositoryImpl): FormatRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
