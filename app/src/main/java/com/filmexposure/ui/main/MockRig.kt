package com.filmexposure.ui.main

/**
 * ВРЕМЕННЫЕ данные вместо реального выбора рига/плёнки (§6.3 — экран выбора ещё не построен,
 * см. договорённость: сначала главный экран поверх тестовых данных). Удалить, когда появится
 * MainViewModel, получающий выбранный риг из RigRepository/SessionRepository.
 */
object MockRig {
    val apertures = listOf("f/2", "f/2.8", "f/4", "f/5.6", "f/8", "f/11", "f/16")
    val shutters = listOf(
        "B", "1", "1/2", "1/4", "1/8", "1/15", "1/30", "1/60",
        "1/125", "1/250", "1/500", "1/1000",
    )
    const val FOCAL_MM = 50f
    const val COC_MM = 0.029f // формат 35mm
    const val FILM_ISO = 400
    const val FILM_LATITUDE_MINUS = 2
    const val FILM_LATITUDE_PLUS = 2
}
