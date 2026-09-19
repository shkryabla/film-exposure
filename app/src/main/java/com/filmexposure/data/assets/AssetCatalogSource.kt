package com.filmexposure.data.assets

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Чтение и парсинг статичных JSON-справочников из assets/ (ТЗ §17 — намеренно не в Room).
 * Результат кэшируется в памяти на время жизни процесса — справочники неизменны в рантайме.
 */
@Singleton
class AssetCatalogSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) {
    private val cameras by lazy { load<CameraSpecJson>("cameras.json") }
    private val lenses by lazy { load<LensSpecJson>("lenses.json") }
    private val films by lazy { load<FilmSpecJson>("films.json") }
    private val formats by lazy { load<FormatSpecJson>("formats.json") }

    fun cameras(): List<CameraSpecJson> = cameras
    fun lenses(): List<LensSpecJson> = lenses
    fun films(): List<FilmSpecJson> = films
    fun formats(): List<FormatSpecJson> = formats

    private inline fun <reified T> load(assetFileName: String): List<T> {
        val json = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
        val type = TypeToken.getParameterized(CatalogFile::class.java, T::class.java).type
        val file: CatalogFile<T> = gson.fromJson(json, type)
        return file.items
    }
}
