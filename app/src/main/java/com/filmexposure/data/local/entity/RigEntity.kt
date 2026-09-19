package com.filmexposure.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rigs")
data class RigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val cameraId: String,
    val lensId: String,
    val activeSpeeds: String,
    val activeApertures: String,
    val focal: Int,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
)
