package com.filmexposure.data.repository

import com.filmexposure.data.datastore.SettingsDataStore
import com.filmexposure.domain.model.AppSettings
import com.filmexposure.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore,
) : SettingsRepository {
    override val settings: Flow<AppSettings> = dataStore.settings
    override suspend fun update(transform: (AppSettings) -> AppSettings) = dataStore.update(transform)
}
