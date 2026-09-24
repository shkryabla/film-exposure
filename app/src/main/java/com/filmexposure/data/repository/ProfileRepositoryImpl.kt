package com.filmexposure.data.repository

import com.filmexposure.data.local.dao.ProfileDao
import com.filmexposure.data.local.entity.ProfileEntity
import com.filmexposure.domain.model.Profile
import com.filmexposure.domain.model.StopStep
import com.filmexposure.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao,
) : ProfileRepository {

    override fun observeAll(): Flow<List<Profile>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Profile? = dao.getById(id)?.toDomain()

    override suspend fun upsert(profile: Profile): Long = dao.upsert(
        ProfileEntity(
            id = profile.id,
            name = profile.name,
            apertures = profile.apertures.joinToString(";"),
            aperturesStep = profile.aperturesStep.name,
            shutters = profile.shutters.joinToString(";"),
            shuttersStep = profile.shuttersStep.name,
            focalMm = profile.focalMm,
            iso = profile.iso,
            formatId = profile.formatId,
        ),
    )

    override suspend fun delete(id: Long) = dao.delete(id)

    private fun ProfileEntity.toDomain() = Profile(
        id = id,
        name = name,
        apertures = apertures.split(";").filter { it.isNotBlank() },
        aperturesStep = runCatching { StopStep.valueOf(aperturesStep) }.getOrDefault(StopStep.FULL),
        shutters = shutters.split(";").filter { it.isNotBlank() },
        shuttersStep = runCatching { StopStep.valueOf(shuttersStep) }.getOrDefault(StopStep.FULL),
        focalMm = focalMm,
        iso = iso,
        formatId = formatId,
    )
}
