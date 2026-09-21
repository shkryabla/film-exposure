package com.filmexposure.ui.main

/**
 * Данные, нужные главному экрану для расчётов: активные выдержки/диафрагмы рига, фокусное,
 * CoC формата, ISO/широта заряженной плёнки. isMock=true — риг и/или плёнка не выбраны в меню,
 * используются значения-заглушки из MockRig (см. договорённость — сначала главный экран поверх
 * тестовых данных, затем реальный выбор).
 */
data class RigContext(
    val apertures: List<String>,
    val shutters: List<String>,
    val focalMm: Float,
    val cocMm: Float,
    val filmIso: Int,
    val filmLatitudeMinus: Int,
    val filmLatitudePlus: Int,
    val isMock: Boolean,
) {
    companion object {
        fun mock() = RigContext(
            apertures = MockRig.apertures,
            shutters = MockRig.shutters,
            focalMm = MockRig.FOCAL_MM,
            cocMm = MockRig.COC_MM,
            filmIso = MockRig.FILM_ISO,
            filmLatitudeMinus = MockRig.FILM_LATITUDE_MINUS,
            filmLatitudePlus = MockRig.FILM_LATITUDE_PLUS,
            isMock = true,
        )
    }
}
