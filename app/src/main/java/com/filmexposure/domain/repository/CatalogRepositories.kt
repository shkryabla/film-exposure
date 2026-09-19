package com.filmexposure.domain.repository

import com.filmexposure.domain.model.Camera
import com.filmexposure.domain.model.Film
import com.filmexposure.domain.model.FilmFormat
import com.filmexposure.domain.model.Lens

/** Справочник камер: встроенный JSON (assets) + пользовательские custom_cameras (Room). */
interface CameraRepository {
    suspend fun getAll(): List<Camera>
    suspend fun addCustom(camera: Camera): String
}

interface LensRepository {
    suspend fun getAll(): List<Lens>
    suspend fun addCustom(lens: Lens): String
}

interface FilmRepository {
    suspend fun getAll(): List<Film>
    suspend fun addCustom(film: Film): String
}

/** Форматы плёнки — только встроенный справочник, без custom (ТЗ §5.1). */
interface FormatRepository {
    suspend fun getAll(): List<FilmFormat>
}
