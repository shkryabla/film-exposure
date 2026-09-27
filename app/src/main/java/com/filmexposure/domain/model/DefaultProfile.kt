package com.filmexposure.domain.model

/**
 * Дефолтный профиль — сидируется в БД при первом запуске (если таблица profiles пуста),
 * а не хранится как константа в памяти: должен быть обычным редактируемым/удаляемым профилем.
 * formatId = "35mm" — совпадает с id из FormatRepositoryImpl.FORMATS.
 */
object DefaultProfile {
    const val NAME = "По умолчанию"
    val APERTURE_SERIES = ApertureSeries.MODERN
    val SHUTTER_SERIES = ShutterSeries.MODERN
    const val FOCAL_MM = 50
    const val FORMAT_ID = "35mm"

    fun create(): Profile = Profile(
        name = NAME,
        apertureSeries = APERTURE_SERIES,
        shutterSeries = SHUTTER_SERIES,
        focalMm = FOCAL_MM,
        formatId = FORMAT_ID,
    )
}
