package com.filmexposure.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.filmexposure.data.local.dao.CustomCameraDao
import com.filmexposure.data.local.dao.CustomFilmDao
import com.filmexposure.data.local.dao.CustomLensDao
import com.filmexposure.data.local.dao.RigDao
import com.filmexposure.data.local.dao.SessionDao
import com.filmexposure.data.local.entity.CustomCameraEntity
import com.filmexposure.data.local.entity.CustomFilmEntity
import com.filmexposure.data.local.entity.CustomLensEntity
import com.filmexposure.data.local.entity.RigEntity
import com.filmexposure.data.local.entity.SessionEntity

/** Справочники (cameras/lenses/films/formats) намеренно НЕ хранятся в Room — только JSON в assets (ТЗ §17). */
@Database(
    entities = [
        CustomCameraEntity::class,
        CustomLensEntity::class,
        CustomFilmEntity::class,
        RigEntity::class,
        SessionEntity::class,
    ],
    version = 2, // v2: добавлено RigEntity.formatId (§6.3 — формат плёнки отдельным полем)
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customCameraDao(): CustomCameraDao
    abstract fun customLensDao(): CustomLensDao
    abstract fun customFilmDao(): CustomFilmDao
    abstract fun rigDao(): RigDao
    abstract fun sessionDao(): SessionDao
}
