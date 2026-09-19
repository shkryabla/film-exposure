package com.filmexposure.domain.model

data class Lens(
    val id: String,
    val name: String,
    val brand: String?,
    val focalMin: Int,
    val focalMax: Int,
    val apertures: List<String>,
    val minFocus: Float,
    val mount: String?,
    val step: String,
    val isCustom: Boolean,
    val notes: String? = null,
)
