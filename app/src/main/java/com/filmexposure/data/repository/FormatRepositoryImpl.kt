package com.filmexposure.data.repository

import com.filmexposure.data.assets.AssetCatalogSource
import com.filmexposure.domain.model.FilmFormat
import com.filmexposure.domain.repository.FormatRepository
import javax.inject.Inject

class FormatRepositoryImpl @Inject constructor(
    private val assets: AssetCatalogSource,
) : FormatRepository {
    override suspend fun getAll(): List<FilmFormat> =
        assets.formats().map { spec ->
            FilmFormat(id = spec.id, name = spec.name, widthMm = spec.width, heightMm = spec.height, coc = spec.coc)
        }
}
