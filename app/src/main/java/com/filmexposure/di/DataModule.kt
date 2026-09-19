package com.filmexposure.di

import android.content.Context
import androidx.room.Room
import com.filmexposure.data.local.AppDatabase
import com.filmexposure.data.local.dao.CustomCameraDao
import com.filmexposure.data.local.dao.CustomFilmDao
import com.filmexposure.data.local.dao.CustomLensDao
import com.filmexposure.data.local.dao.RigDao
import com.filmexposure.data.local.dao.SessionDao
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DI для data-слоя (Room, DataStore, Gson). Биндинги repository-интерфейсов (domain ↔ data) —
 * отдельным модулем на этапе UI/ViewModel (ТЗ §18, шаг 8), когда появятся первые потребители.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "film-exposure.db").build()

    @Provides
    fun provideCustomCameraDao(db: AppDatabase): CustomCameraDao = db.customCameraDao()

    @Provides
    fun provideCustomLensDao(db: AppDatabase): CustomLensDao = db.customLensDao()

    @Provides
    fun provideCustomFilmDao(db: AppDatabase): CustomFilmDao = db.customFilmDao()

    @Provides
    fun provideRigDao(db: AppDatabase): RigDao = db.rigDao()

    @Provides
    fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
}
