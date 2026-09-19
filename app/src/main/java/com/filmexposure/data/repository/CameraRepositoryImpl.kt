package com.filmexposure.data.repository

import com.filmexposure.data.assets.AssetCatalogSource
import com.filmexposure.data.local.dao.CustomCameraDao
import com.filmexposure.data.local.entity.CustomCameraEntity
import com.filmexposure.domain.model.Camera
import com.filmexposure.domain.repository.CameraRepository
import javax.inject.Inject

private fun splitList(raw: String): List<String> =
    raw.split(";").map { it.trim() }.filter { it.isNotBlank() }

class CameraRepositoryImpl @Inject constructor(
    private val assets: AssetCatalogSource,
    private val dao: CustomCameraDao,
) : CameraRepository {

    override suspend fun getAll(): List<Camera> {
        val builtIn = assets.cameras().map { spec ->
            Camera(
                id = spec.id,
                name = spec.name,
                brand = spec.brand,
                formatId = spec.format,
                shutterType = spec.shutterType,
                speeds = spec.speeds,
                xSync = spec.xSync,
                step = spec.step,
                isCustom = false,
            )
        }
        val custom = dao.getAll().map { it.toDomain() }
        return builtIn + custom
    }

    override suspend fun addCustom(camera: Camera): String {
        val entity = CustomCameraEntity(
            name = camera.name,
            formatId = camera.formatId,
            shutterType = camera.shutterType,
            speeds = camera.speeds.joinToString(";"),
            xSync = camera.xSync,
            step = camera.step,
            notes = camera.notes.orEmpty(),
            createdAt = System.currentTimeMillis(),
        )
        val id = dao.insert(entity)
        return "custom_$id"
    }

    private fun CustomCameraEntity.toDomain() = Camera(
        id = "custom_$id",
        name = name,
        brand = null,
        formatId = formatId,
        shutterType = shutterType,
        speeds = splitList(speeds),
        xSync = xSync,
        step = step,
        isCustom = true,
        notes = notes,
    )
}
