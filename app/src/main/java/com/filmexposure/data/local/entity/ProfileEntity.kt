package com.filmexposure.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
/** Профиль — заменяет камеру+объектив+плёнку+риг, целиком заполняется пользователем. */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val apertureSeries: String, // ApertureSeries.name
    val shutterSeries: String,  // ShutterSeries.name
    val focalMm: Int,
    val formatId: String,
)
