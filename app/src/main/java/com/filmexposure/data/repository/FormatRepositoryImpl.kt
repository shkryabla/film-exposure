package com.filmexposure.data.repository

import com.filmexposure.domain.model.FilmFormat
import com.filmexposure.domain.repository.FormatRepository
import javax.inject.Inject

/**
 * Форматы кадра — небольшой захардкоженный список физических констант (CoC для ГРИП), не БД
 * и не JSON-справочник (решение по проекту при переходе на Профили).
 */
class FormatRepositoryImpl @Inject constructor() : FormatRepository {
    override fun getAll(): List<FilmFormat> = FORMATS

    companion object {
        private val FORMATS = listOf(
            FilmFormat(id = "35mm", name = "35 мм", widthMm = 36f, heightMm = 24f, coc = 0.029f),
            FilmFormat(id = "6x45", name = "6×4.5", widthMm = 56f, heightMm = 41.5f, coc = 0.040f),
            FilmFormat(id = "6x6", name = "6×6", widthMm = 56f, heightMm = 56f, coc = 0.045f),
            FilmFormat(id = "6x7", name = "6×7", widthMm = 70f, heightMm = 56f, coc = 0.055f),
            FilmFormat(id = "4x5", name = "4×5\"", widthMm = 127f, heightMm = 102f, coc = 0.100f),
            FilmFormat(id = "8x10", name = "8×10\"", widthMm = 254f, heightMm = 203f, coc = 0.200f),
        )
    }
}
