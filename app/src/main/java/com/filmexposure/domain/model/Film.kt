package com.filmexposure.domain.model

data class Film(
    val id: String,
    val name: String,
    val brand: String?,
    val iso: Int,
    val type: String,
    val latitudeMinus: Int,
    val latitudePlus: Int,
    val pushMax: Int,
    val reciprocity: ReciprocityTable,
    val isCustom: Boolean,
    val notes: String? = null,
)
