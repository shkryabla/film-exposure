package com.filmexposure.di

import com.filmexposure.data.repository.CameraRepositoryImpl
import com.filmexposure.data.repository.FilmRepositoryImpl
import com.filmexposure.data.repository.FormatRepositoryImpl
import com.filmexposure.data.repository.LensRepositoryImpl
import com.filmexposure.data.repository.RigRepositoryImpl
import com.filmexposure.data.repository.SessionRepositoryImpl
import com.filmexposure.data.repository.SettingsRepositoryImpl
import com.filmexposure.domain.repository.CameraRepository
import com.filmexposure.domain.repository.FilmRepository
import com.filmexposure.domain.repository.FormatRepository
import com.filmexposure.domain.repository.LensRepository
import com.filmexposure.domain.repository.RigRepository
import com.filmexposure.domain.repository.SessionRepository
import com.filmexposure.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCameraRepository(impl: CameraRepositoryImpl): CameraRepository

    @Binds
    abstract fun bindLensRepository(impl: LensRepositoryImpl): LensRepository

    @Binds
    abstract fun bindFilmRepository(impl: FilmRepositoryImpl): FilmRepository

    @Binds
    abstract fun bindFormatRepository(impl: FormatRepositoryImpl): FormatRepository

    @Binds
    abstract fun bindRigRepository(impl: RigRepositoryImpl): RigRepository

    @Binds
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
