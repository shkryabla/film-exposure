package com.filmexposure.domain.repository

import com.filmexposure.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeAll(): Flow<List<Profile>>
    suspend fun getById(id: Long): Profile?
    suspend fun upsert(profile: Profile): Long
    suspend fun delete(id: Long)
}
