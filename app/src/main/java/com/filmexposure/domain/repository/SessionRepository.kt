package com.filmexposure.domain.repository

import com.filmexposure.domain.model.Session

interface SessionRepository {
    suspend fun startSession(session: Session): Long
}
