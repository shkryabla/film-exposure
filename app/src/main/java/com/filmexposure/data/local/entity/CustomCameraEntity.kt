package com.filmexposure.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** speeds — значения через ";" (см. парсер списков, ТЗ §6.4). */
@Entity(tableName = "custom_cameras")
data class CustomCameraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val formatId: String,
    val shutterType: String,
    val speeds: String,
    val xSync: String?,
    val step: String,
    val notes: String,
    val createdAt: Long,
)
