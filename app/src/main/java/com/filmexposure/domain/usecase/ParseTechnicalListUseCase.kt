package com.filmexposure.domain.usecase

import javax.inject.Inject

/**
 * Парсер списков выдержек/диафрагм через ";" (ТЗ §6.4).
 * Правила: "B" — Bulb; "1s" → "1"; голое число диафрагмы → "f/N"; дедупликация;
 * выдержки сортируются от длинных к коротким, диафрагмы — от открытых к закрытым.
 */
class ParseTechnicalListUseCase @Inject constructor() {

    fun parseSpeeds(raw: String): List<String> {
        val normalized = raw.split(";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { normalizeSpeedToken(it) }
            .distinct()

        return normalized.sortedByDescending { speedSortKey(it) }
    }

    fun parseApertures(raw: String): List<String> {
        val normalized = raw.split(";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { normalizeApertureToken(it) }
            .distinct()

        return normalized.sortedBy { apertureValue(it) ?: Float.MAX_VALUE }
    }

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

    /** f-число по метке "f/N" (или голому "N"). */
    fun apertureValue(label: String): Float? =
        label.removePrefix("f/").removePrefix("F/").toFloatOrNull()

    private fun normalizeSpeedToken(token: String): String {
        if (token.equals("B", ignoreCase = true)) return "B"
        // "1s" -> "1" (секунды без суффикса — уже наш внутренний формат)
        val sSuffix = Regex("""^(\d+(?:[.,]\d+)?)[sS]$""")
        sSuffix.find(token)?.let { return it.groupValues[1].replace(",", ".") }
        return token.replace(",", ".")
    }

    private fun normalizeApertureToken(token: String): String {
        if (token.startsWith("f/", ignoreCase = true)) {
            return "f/" + token.substring(2).replace(",", ".")
        }
        return "f/" + token.replace(",", ".")
    }

    /** Ключ сортировки выдержек: Bulb — "бесконечно длинная", т.е. сортируется первой. */
    private fun speedSortKey(label: String): Float =
        shutterSeconds(label) ?: Float.POSITIVE_INFINITY
}
