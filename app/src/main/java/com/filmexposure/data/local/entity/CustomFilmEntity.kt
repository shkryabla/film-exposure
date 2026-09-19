package com.filmexposure.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** reciprocityRaw — сериализованная (JSON) таблица {metered, actual}, вводится/парсится на экране custom-плёнки. */
@Entity(tableName = "custom_films")
data class CustomFilmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iso: Int,
    val type: String,
    val latitudeMinus: Int,
    val latitudePlus: Int,
    val pushMax: Int,
    val reciprocityRaw: String,
    val createdAt: Long,
)
