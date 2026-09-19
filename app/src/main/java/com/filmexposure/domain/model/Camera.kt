package com.filmexposure.domain.model

data class Camera(
    val id: String,
    val name: String,
    val brand: String?,
    val formatId: String,
    val shutterType: String,
    val speeds: List<String>,
    val xSync: String?,
    val step: String,
    val isCustom: Boolean,
    val notes: String? = null,
)
