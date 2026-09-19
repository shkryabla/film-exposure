package com.filmexposure.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.filmexposure.data.local.entity.CustomCameraEntity
import com.filmexposure.data.local.entity.CustomFilmEntity
import com.filmexposure.data.local.entity.CustomLensEntity

@Dao
interface CustomCameraDao {
    @Query("SELECT * FROM custom_cameras ORDER BY name")
    suspend fun getAll(): List<CustomCameraEntity>

    @Insert
    suspend fun insert(entity: CustomCameraEntity): Long
}

@Dao
interface CustomLensDao {
    @Query("SELECT * FROM custom_lenses ORDER BY name")
    suspend fun getAll(): List<CustomLensEntity>

    @Insert
    suspend fun insert(entity: CustomLensEntity): Long
}

@Dao
interface CustomFilmDao {
    @Query("SELECT * FROM custom_films ORDER BY name")
    suspend fun getAll(): List<CustomFilmEntity>

    @Insert
    suspend fun insert(entity: CustomFilmEntity): Long
}
