package com.filmexposure.domain.model

/** Сессия съёмки — риг + заряженная плёнка (после удаления фильтра/макро/evCorrection — см. договорённости). */
data class Session(
    val id: Long = 0,
    val rigId: Long,
    val filmId: String,
    val isoPushed: Int,
)
