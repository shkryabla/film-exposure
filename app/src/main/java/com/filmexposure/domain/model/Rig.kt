package com.filmexposure.domain.model

/** Риг — связка камера + объектив + активный поднабор выдержек/диафрагм (ТЗ §5.2, §6.3). */
data class Rig(
    val id: Long = 0,
    val name: String,
    val cameraId: String,
    val lensId: String,
    val activeSpeeds: List<String>,
    val activeApertures: List<String>,
    val focal: Int,
    val notes: String,
)
