package com.filmexposure.domain.model

/** Шаг ряда диафрагм/выдержек — сейчас только тег для отображения, без автогенерации ряда. */
enum class StopStep { FULL, HALF, THIRD }

/**
 * Профиль — всё, что нужно для расчёта пар и ГРИП, целиком заполняется пользователем
 * (замена камеры+объектива+плёнки+рига и всех справочников техники).
 */
data class Profile(
    val id: Long = 0,
    val name: String,
    val apertures: List<String>,
    val aperturesStep: StopStep,
    val shutters: List<String>,
    val shuttersStep: StopStep,
    val focalMm: Int,
    val iso: Int,
    val formatId: String,
)
