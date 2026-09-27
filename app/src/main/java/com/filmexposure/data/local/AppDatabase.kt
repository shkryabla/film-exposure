package com.filmexposure.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import com.filmexposure.data.local.dao.ProfileDao
import com.filmexposure.data.local.entity.ProfileEntity
/** Единственная сущность в Room — пользовательские Профили. Форматы кадра — константы в коде. */
@Database(
    entities = [ProfileEntity::class],
    // v4: apertures/shutters/StopStep/iso вырезаны из Profile — вместо них apertureSeries/shutterSeries
    // (готовые захардкоженные ряды), iso переехал в AppSettings. fallbackToDestructiveMigration
    // требует бампа версии при любом изменении схемы, иначе Room падает на несовпадении схемы.
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
