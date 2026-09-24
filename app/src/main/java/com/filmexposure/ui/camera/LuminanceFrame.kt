package com.filmexposure.ui.camera

/**
 * Данные, нужные для §4.2: S (сигнал), t0/N0/ISO0 — параметры экспозиции ТЕЛЕФОНА в момент кадра.
 *
 * ВАЖНО (упрощение v1): luminance и exposure-параметры обновляются двумя независимыми путями
 * (ImageAnalysis.Analyzer и Camera2 CaptureCallback) и объединяются по "последнему известному
 * значению", а не по точному соответствию кадр-в-кадр. Для ручного замера (тап, пауза кадра)
 * рассинхронизация в пределах десятков мс несущественна — сцена почти не меняется мгновенно.
 * Если понадобится точная purity, нужно доставать метаданные из самого ImageProxy
 * (ImageInfo.getTimestamp()) и сопоставлять с CaptureResult.timestamp напрямую.
 */
data class LuminanceFrame(
    val averageLuminance: Float, // 0..255, среднее по Y-плоскости (до вычитания чёрного — §8.1 п.2)
    val exposureTimeSeconds: Float, // t0
    val aperture: Float, // N0 (f-число объектива телефона)
    val isoSensitivity: Int, // ISO0
)
