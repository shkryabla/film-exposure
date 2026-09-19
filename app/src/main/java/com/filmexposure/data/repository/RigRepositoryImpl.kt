package com.filmexposure.data.repository

import com.filmexposure.data.local.dao.RigDao
import com.filmexposure.data.local.entity.RigEntity
import com.filmexposure.domain.model.Rig
import com.filmexposure.domain.repository.RigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RigRepositoryImpl @Inject constructor(
    private val dao: RigDao,
) : RigRepository {

    override fun observeAll(): Flow<List<Rig>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun upsert(rig: Rig): Long {
        val now = System.currentTimeMillis()
        // REPLACE-стратегия физически удаляет и заново вставляет строку — без явного сохранения
        // исходного createdAt апдейт рига каждый раз "старил бы" его заново.
        val createdAt = if (rig.id == 0L) now else dao.getById(rig.id)?.createdAt ?: now
        val entity = RigEntity(
            id = rig.id,
            name = rig.name,
            cameraId = rig.cameraId,
            lensId = rig.lensId,
            activeSpeeds = rig.activeSpeeds.joinToString(";"),
            activeApertures = rig.activeApertures.joinToString(";"),
            focal = rig.focal,
            notes = rig.notes,
            createdAt = createdAt,
            updatedAt = now,
        )
        return dao.upsert(entity)
    }

    override suspend fun delete(rigId: Long) = dao.delete(rigId)

    private fun RigEntity.toDomain() = Rig(
        id = id,
        name = name,
        cameraId = cameraId,
        lensId = lensId,
        activeSpeeds = activeSpeeds.split(";").map { it.trim() }.filter { it.isNotBlank() },
        activeApertures = activeApertures.split(";").map { it.trim() }.filter { it.isNotBlank() },
        focal = focal,
        notes = notes,
    )
}
