package com.filmexposure.data.repository
import com.filmexposure.data.local.dao.ProfileDao
import com.filmexposure.data.local.entity.ProfileEntity
import com.filmexposure.domain.model.ApertureSeries
import com.filmexposure.domain.model.DefaultProfile
import com.filmexposure.domain.model.Profile
import com.filmexposure.domain.model.ShutterSeries
import com.filmexposure.domain.repository.ProfileRepository
import com.filmexposure.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao,
    private val settingsRepository: SettingsRepository,
) : ProfileRepository {
    override fun observeAll(): Flow<List<Profile>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override suspend fun getById(id: Long): Profile? = dao.getById(id)?.toDomain()
    override suspend fun upsert(profile: Profile): Long = dao.upsert(profile.toEntity())
    override suspend fun delete(id: Long) = dao.delete(id)
    /**
     * Сидирование дефолтного профиля при первом запуске (таблица пуста) — обычный редактируемый
     * профиль, а не константа в памяти. Сразу становится активным (selectedProfileId), иначе
     * главный экран показывал бы "профиль не выбран" после чистой установки.
     */
    override suspend fun seedDefaultProfileIfEmpty() {
        if (dao.count() == 0) {
            val id = dao.upsert(DefaultProfile.create().toEntity())
            settingsRepository.update { it.copy(selectedProfileId = id) }
        }
    }
    private fun Profile.toEntity() = ProfileEntity(
        id = id,
        name = name,
        apertureSeries = apertureSeries.name,
        shutterSeries = shutterSeries.name,
        focalMm = focalMm,
        formatId = formatId,
    )
    private fun ProfileEntity.toDomain() = Profile(
        id = id,
        name = name,
        apertureSeries = runCatching { ApertureSeries.valueOf(apertureSeries) }.getOrDefault(ApertureSeries.MODERN),
        shutterSeries = runCatching { ShutterSeries.valueOf(shutterSeries) }.getOrDefault(ShutterSeries.MODERN),
        focalMm = focalMm,
        formatId = formatId,
    )
}
