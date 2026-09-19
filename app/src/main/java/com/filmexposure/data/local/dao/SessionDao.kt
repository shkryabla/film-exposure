package com.filmexposure.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.filmexposure.data.local.entity.SessionEntity

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(entity: SessionEntity): Long
}
