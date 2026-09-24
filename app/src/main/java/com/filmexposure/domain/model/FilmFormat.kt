package com.filmexposure.domain.model

/** Формат плёнки/кадра (35 мм, 6×6, 4×5" и т.д.) — только встроенный справочник, без custom. */
data class FilmFormat(
    val id: String,
    val name: String,
    val widthMm: Float,
    val heightMm: Float,
    val coc: Float,
)
