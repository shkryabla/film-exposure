package com.filmexposure.data.repository

import com.filmexposure.data.local.dao.SessionDao
import com.filmexposure.data.local.entity.SessionEntity
import com.filmexposure.domain.model.Session
import com.filmexposure.domain.repository.SessionRepository
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val dao: SessionDao,
) : SessionRepository {
    override suspend fun startSession(session: Session): Long {
        val entity = SessionEntity(
            rigId = session.rigId,
            filmId = session.filmId,
            isoPushed = session.isoPushed,
            createdAt = System.currentTimeMillis(),
        )
        return dao.insert(entity)
    }
}
