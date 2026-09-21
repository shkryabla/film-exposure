package com.filmexposure.domain.model

/**
 * Риг — связка камера + объектив + формат плёнки + активный поднабор выдержек/диафрагм
 * (ТЗ §5.2, §6.3). formatId — отдельное поле (не берётся автоматически из камеры): у камеры
 * может быть сменная задняя часть/формат, ТЗ §6.3 явно требует отдельный дропдаун формата.
 */
data class Rig(
    val id: Long = 0,
    val name: String,
    val cameraId: String,
    val lensId: String,
    val formatId: String,
    val activeSpeeds: List<String>,
    val activeApertures: List<String>,
    val focal: Int,
    val notes: String,
)
