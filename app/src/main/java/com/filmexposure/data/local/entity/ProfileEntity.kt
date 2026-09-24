package com.filmexposure.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Профиль — заменяет камеру+объектив+плёнку+риг, целиком заполняется пользователем. */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val apertures: String, // через ";"
    val aperturesStep: String,
    val shutters: String, // через ";"
    val shuttersStep: String,
    val focalMm: Int,
    val iso: Int,
    val formatId: String,
)
