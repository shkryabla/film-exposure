package com.filmexposure.domain.repository

import com.filmexposure.domain.model.Rig
import kotlinx.coroutines.flow.Flow

interface RigRepository {
    fun observeAll(): Flow<List<Rig>>
    suspend fun upsert(rig: Rig): Long
    suspend fun delete(rigId: Long)
}
