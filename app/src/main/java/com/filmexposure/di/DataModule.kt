package com.filmexposure.di

import android.content.Context
import androidx.room.Room
import com.filmexposure.data.local.AppDatabase
import com.filmexposure.data.local.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "film-exposure.db")
            // Схема ещё активно меняется на этапе разработки (нет реальных пользовательских
            // данных) — при бампе схемы проще пересоздать БД, чем поддерживать миграции.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()
}
