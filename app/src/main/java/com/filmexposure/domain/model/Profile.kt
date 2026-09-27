package com.filmexposure.domain.model

/**
 * Ряд диафрагм — жёстко захардкожен в коде (как FilmFormat), без ручного ввода и без half/third-stop:
 * пользователь просто выбирает готовую серию.
 */
enum class ApertureSeries { MODERN, OLD_EUROPEAN }

object ApertureSeriesData {
    val MODERN = listOf("f/1.4", "f/2", "f/2.8", "f/4", "f/5.6", "f/8", "f/11", "f/16", "f/22")
    val OLD_EUROPEAN = listOf("f/1.1", "f/1.6", "f/2.2", "f/3.2", "f/4.5", "f/6.3", "f/9", "f/12.5", "f/18")

    fun valuesFor(series: ApertureSeries): List<String> = when (series) {
        ApertureSeries.MODERN -> MODERN
        ApertureSeries.OLD_EUROPEAN -> OLD_EUROPEAN
    }
}

/** Ряд выдержек — аналогично ApertureSeries, полный стоп, без ручного ввода. */
enum class ShutterSeries { MODERN, OLD_EUROPEAN }

object ShutterSeriesData {
    val MODERN = listOf("1", "1/2", "1/4", "1/8", "1/15", "1/30", "1/60", "1/125", "1/250", "1/500", "1/1000", "1/2000", "1/4000")
    val OLD_EUROPEAN = listOf("1", "1/2", "1/5", "1/10", "1/25", "1/50", "1/100", "1/250", "1/500", "1/1250")

    fun valuesFor(series: ShutterSeries): List<String> = when (series) {
        ShutterSeries.MODERN -> MODERN
        ShutterSeries.OLD_EUROPEAN -> OLD_EUROPEAN
    }
}

/**
 * Профиль — всё, что нужно для расчёта пар и ГРИП, целиком заполняется пользователем
 * (замена камеры+объектива+плёнки+рига и всех справочников техники).
 *
 * ISO сюда больше не входит (решение по проекту) — ISO выбирается на главном экране и живёт
 * в AppSettings.filmIso, т.к. это параметр текущей съёмки, а не характеристика рига.
 */
data class Profile(
    val id: Long = 0,
    val name: String,
    val apertureSeries: ApertureSeries,
    val shutterSeries: ShutterSeries,
    val focalMm: Int,
    val formatId: String,
)
