package com.filmexposure.data.repository

import com.filmexposure.data.assets.AssetCatalogSource
import com.filmexposure.data.assets.ReciprocityJson
import com.filmexposure.data.assets.ReciprocityPointJson
import com.filmexposure.data.local.dao.CustomFilmDao
import com.filmexposure.data.local.entity.CustomFilmEntity
import com.filmexposure.domain.model.Film
import com.filmexposure.domain.model.ReciprocityPoint
import com.filmexposure.domain.model.ReciprocityTable
import com.filmexposure.domain.repository.FilmRepository
import com.google.gson.Gson
import javax.inject.Inject

/** reciprocityRaw в custom_films хранит Gson-JSON того же вида, что и films.json → reciprocity. */
class FilmRepositoryImpl @Inject constructor(
    private val assets: AssetCatalogSource,
    private val dao: CustomFilmDao,
    private val gson: Gson,
) : FilmRepository {

    override suspend fun getAll(): List<Film> {
        val builtIn = assets.films().map { spec ->
            Film(
                id = spec.id,
                name = spec.name,
                brand = spec.brand,
                iso = spec.iso,
                type = spec.type,
                latitudeMinus = spec.latitude.minus,
                latitudePlus = spec.latitude.plus,
                pushMax = spec.pushMax,
                reciprocity = ReciprocityTable(
                    threshold = spec.reciprocity.threshold,
                    points = spec.reciprocity.table.map { ReciprocityPoint(it.metered, it.actual) },
                ),
                isCustom = false,
            )
        }
        val custom = dao.getAll().map { it.toDomain() }
        return builtIn + custom
    }

    override suspend fun addCustom(film: Film): String {
        val reciprocityJson = ReciprocityJson(
            threshold = film.reciprocity.threshold,
            table = film.reciprocity.points.map { ReciprocityPointJson(it.metered, it.actual) },
        )
        val entity = CustomFilmEntity(
            name = film.name,
            iso = film.iso,
            type = film.type,
            latitudeMinus = film.latitudeMinus,
            latitudePlus = film.latitudePlus,
            pushMax = film.pushMax,
            reciprocityRaw = gson.toJson(reciprocityJson),
            createdAt = System.currentTimeMillis(),
        )
        val id = dao.insert(entity)
        return "custom_$id"
    }

    private fun CustomFilmEntity.toDomain(): Film {
        val reciprocity = runCatching { gson.fromJson(reciprocityRaw, ReciprocityJson::class.java) }
            .getOrNull()
            ?: ReciprocityJson(threshold = 1f, table = emptyList())
        return Film(
            id = "custom_$id",
            name = name,
            brand = null,
            iso = iso,
            type = type,
            latitudeMinus = latitudeMinus,
            latitudePlus = latitudePlus,
            pushMax = pushMax,
            reciprocity = ReciprocityTable(
                threshold = reciprocity.threshold,
                points = reciprocity.table.map { ReciprocityPoint(it.metered, it.actual) },
            ),
            isCustom = true,
        )
    }
}
