package com.filmexposure.domain.repository

import com.filmexposure.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun update(transform: (AppSettings) -> AppSettings)
}
