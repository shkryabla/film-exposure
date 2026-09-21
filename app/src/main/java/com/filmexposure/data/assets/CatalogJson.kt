package com.filmexposure.data.assets

/** DTO 1:1 со схемами JSON-справочников (ТЗ §5.1). Поля названы как в JSON — Gson мапит без аннотаций. */

data class CatalogFile<T>(
    val version: Int,
    val items: List<T>,
)

data class CameraSpecJson(
    val id: String,
    val name: String,
    val brand: String?,
    val country: String?,
    val format: String,
    val shutterType: String,
    val speeds: List<String>,
    val xSync: String?,
    val step: String,
    val notes: String? = null,
)

data class LensSpecJson(
    val id: String,
    val name: String,
    val brand: String?,
    val country: String?,
    val focalMin: Int,
    val focalMax: Int,
    val apertures: List<String>,
    val minFocus: Float,
    val mount: String?,
    val step: String,
)

data class ReciprocityPointJson(val metered: Float, val actual: Float)

data class ReciprocityJson(val threshold: Float, val table: List<ReciprocityPointJson>)

data class FilmLatitudeJson(val minus: Int, val plus: Int)

data class FilmSpecJson(
    val id: String,
    val name: String,
    val brand: String?,
    val iso: Int,
    val type: String,
    val latitude: FilmLatitudeJson,
    val pushMax: Int,
    val reciprocity: ReciprocityJson,
)

data class FormatSpecJson(
    val id: String,
    val name: String,
    val width: Float,
    val height: Float,
    val coc: Float,
)
