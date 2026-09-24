package com.filmexposure.domain.repository

import com.filmexposure.domain.model.FilmFormat

/** Форматы кадра — небольшой список констант в коде (не БД, не JSON — см. решение по проекту). */
interface FormatRepository {
    fun getAll(): List<FilmFormat>
}
