package com.filmexposure.ui.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

/**
 * Считает среднюю яркость кадра по Y-плоскости YUV_420_888 (по умолчанию для ImageAnalysis).
 * Полный расчёт по точке тапа 5×5 пикселей (§8.1) и вычитание чёрного уровня — на этапе
 * подключения к экрану замера; здесь — усреднение по всему кадру, этого достаточно для
 * калибровки (эталон обычно занимает всё поле кадра).
 */
class LuminanceAnalyzer(
    private val onLuminance: (Float) -> Unit,
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        try {
            val yPlane = image.planes.getOrNull(0)
            if (yPlane == null) {
                image.close()
                return
            }
            val buffer = yPlane.buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            var sum = 0L
            // Шаг 4 байта — не нужен точный пиксель-в-пиксель для среднего, ускоряет расчёт на слабых телефонах.
            var count = 0
            var i = 0
            while (i < bytes.size) {
                sum += bytes[i].toInt() and 0xFF
                count++
                i += 4
            }
            val average = if (count > 0) sum.toFloat() / count else 0f
            onLuminance(average)
        } finally {
            image.close()
        }
    }
}
