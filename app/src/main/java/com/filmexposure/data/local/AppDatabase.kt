package com.filmexposure.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.filmexposure.data.local.dao.ProfileDao
import com.filmexposure.data.local.entity.ProfileEntity

/** Единственная сущность в Room — пользовательские Профили. Форматы кадра — константы в коде. */
@Database(
    entities = [ProfileEntity::class],
    version = 3, // v3: переход на модель "Профиль" — вся прежняя схема (риги/камеры/объективы/плёнки/сессии) вырезана
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
