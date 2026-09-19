package com.filmexposure.domain.model

/** Одна пара {замеренная выдержка → реальная выдержка} из таблицы взаимозаместимости плёнки. */
data class ReciprocityPoint(val metered: Float, val actual: Float)

/**
 * Таблица взаимозаместимости (закон Шварцшильда, ТЗ §4.3, §5.1).
 * threshold — порог в секундах, ниже которого коррекция не нужна.
 * Заполняется для всех плёнок без исключения (в т.ч. цветных — решение по проекту).
 */
data class ReciprocityTable(
    val threshold: Float,
    val points: List<ReciprocityPoint>,
)
