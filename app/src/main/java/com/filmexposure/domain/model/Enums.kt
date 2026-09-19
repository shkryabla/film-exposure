package com.filmexposure.domain.model

/** Категория точки замера (ТЗ §4.5). */
enum class PointCategory { SHADOW, MID, LIGHT }

enum class DistanceUnit { METERS, FEET }

/** Слой шкалы стопов: Simple — иконки для новичков, Pro — цифры −3…+3 (ТЗ §7.3). */
enum class ScaleMode { SIMPLE, PRO }

enum class ButtonSide { LEFT, RIGHT }

/**
 * Режим посадки экспозиции (ТЗ §5.5). Недоступен при 1 точке замера —
 * см. [com.filmexposure.domain.usecase.CalculateExposurePostingUseCase].
 */
enum class ExposurePosting { SHADOWS, HIGHLIGHTS, BALANCE }
