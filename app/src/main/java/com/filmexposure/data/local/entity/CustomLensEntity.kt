package com.filmexposure.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_lenses")
data class CustomLensEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val focalMin: Int,
    val focalMax: Int,
    val apertures: String,
    val minFocus: Float,
    val mount: String?,
    val step: String,
    val notes: String,
    val createdAt: Long,
)
