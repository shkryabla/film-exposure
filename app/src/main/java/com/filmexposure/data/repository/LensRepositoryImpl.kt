package com.filmexposure.data.repository

import com.filmexposure.data.assets.AssetCatalogSource
import com.filmexposure.data.local.dao.CustomLensDao
import com.filmexposure.data.local.entity.CustomLensEntity
import com.filmexposure.domain.model.Lens
import com.filmexposure.domain.repository.LensRepository
import javax.inject.Inject

class LensRepositoryImpl @Inject constructor(
    private val assets: AssetCatalogSource,
    private val dao: CustomLensDao,
) : LensRepository {

    override suspend fun getAll(): List<Lens> {
        val builtIn = assets.lenses().map { spec ->
            Lens(
                id = spec.id,
                name = spec.name,
                brand = spec.brand,
                focalMin = spec.focalMin,
                focalMax = spec.focalMax,
                apertures = spec.apertures,
                minFocus = spec.minFocus,
                mount = spec.mount,
                step = spec.step,
                isCustom = false,
            )
        }
        val custom = dao.getAll().map { it.toDomain() }
        return builtIn + custom
    }

    override suspend fun addCustom(lens: Lens): String {
        val entity = CustomLensEntity(
            name = lens.name,
            focalMin = lens.focalMin,
            focalMax = lens.focalMax,
            apertures = lens.apertures.joinToString(";"),
            minFocus = lens.minFocus,
            mount = lens.mount,
            step = lens.step,
            notes = lens.notes.orEmpty(),
            createdAt = System.currentTimeMillis(),
        )
        val id = dao.insert(entity)
        return "custom_$id"
    }

    private fun CustomLensEntity.toDomain() = Lens(
        id = "custom_$id",
        name = name,
        brand = null,
        focalMin = focalMin,
        focalMax = focalMax,
        apertures = apertures.split(";").map { it.trim() }.filter { it.isNotBlank() },
        minFocus = minFocus,
        mount = mount,
        step = step,
        isCustom = true,
        notes = notes,
    )
}
