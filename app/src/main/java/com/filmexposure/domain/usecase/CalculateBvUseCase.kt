package com.filmexposure.domain.usecase

import com.filmexposure.domain.model.ApexValues
import javax.inject.Inject
import kotlin.math.log2

/**
 * Перевод сигнала матрицы телефона в Bv, затем в Ev сцены (ТЗ §4.1, §4.2).
 *
 * Bv = log2(S · N0² / (t0 · ISO0)) + C
 * Ev(на рабочем ISO плёнки) = Bv + Sv(iso)
 *
 * S, t0, N0, ISO0 — сигнал и параметры экспозиции ТЕЛЕФОНА в момент замера (не риг/плёнка
 * пользователя). C — калибровочная константа с экрана калибровки (AppSettings.calibrationConstant).
 */
class CalculateBvUseCase @Inject constructor() {

    fun bv(signal: Float, t0: Float, n0: Float, iso0: Int, calibrationConstant: Float): Float =
        log2(signal * n0 * n0 / (t0 * iso0)) + calibrationConstant

    fun evAtFilmIso(bv: Float, filmIso: Int): Float = bv + ApexValues.sv(filmIso)
}
