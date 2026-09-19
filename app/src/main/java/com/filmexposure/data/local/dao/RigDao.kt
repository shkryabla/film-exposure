package com.filmexposure.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.filmexposure.data.local.entity.RigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RigDao {
    @Query("SELECT * FROM rigs ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<RigEntity>>

    @Query("SELECT * FROM rigs WHERE id = :id")
    suspend fun getById(id: Long): RigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RigEntity): Long

    @Query("DELETE FROM rigs WHERE id = :rigId")
    suspend fun delete(rigId: Long)
}
