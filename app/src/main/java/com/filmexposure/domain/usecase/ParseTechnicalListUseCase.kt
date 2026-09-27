package com.filmexposure.domain.usecase
import javax.inject.Inject
/**
 * Конвертация меток из захардкоженных рядов (ApertureSeriesData/ShutterSeriesData, §5.1) в
 * числовые значения для APEX-расчётов. Ручной парсинг ";"-списков (parseSpeeds/parseApertures)
 * убран решением по проекту — свободный ввод отменён, ряды теперь фиксированные.
 */
class ParseTechnicalListUseCase @Inject constructor() {
    /** Секунды выдержки по её метке; null для "B" (Bulb — ручная, не участвует в расчёте Ev). */
    fun shutterSeconds(label: String): Float? {
        if (label.equals("B", ignoreCase = true)) return null
        return if (label.contains("/")) {
            val parts = label.split("/")
            val num = parts.getOrNull(0)?.toFloatOrNull()
            val den = parts.getOrNull(1)?.toFloatOrNull()
            if (num != null && den != null && den != 0f) num / den else null
        } else {
            label.toFloatOrNull()
        }
    }
    /** f-число по метке "f/N". */
    fun apertureValue(label: String): Float? =
        label.removePrefix("f/").removePrefix("F/").toFloatOrNull()
}
