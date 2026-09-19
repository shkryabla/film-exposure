package com.filmexposure.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Урезано по решению проекта: без filterFactor/macroM/evCorrection (фильтры и макро вырезаны). */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rigId: Long,
    val filmId: String,
    val isoPushed: Int,
    val createdAt: Long,
)
