package com.filmexposure
import android.app.Application
import com.filmexposure.domain.repository.ProfileRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * Точка входа DI-графа (Hilt). При старте сидирует дефолтный профиль (§5.1, DefaultProfile),
 * если БД пустая — до первой отрисовки MainScreen, чтобы не мелькала плашка "профиль не выбран"
 * на чистой установке.
 */
@HiltAndroidApp
class FilmExposureApp : Application() {
    @Inject lateinit var profileRepository: ProfileRepository
    // SupervisorJob — сбой сидирования не должен ронять остальные корутины уровня приложения
    // (их пока нет, но это дефолтная безопасная практика для application-scope).
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        appScope.launch { profileRepository.seedDefaultProfileIfEmpty() }
    }
}
