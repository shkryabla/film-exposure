package com.filmexposure

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Точка входа DI-графа (Hilt). Наполнение модулями — на этапе "DI" (см. ТЗ §18, шаг 8).
 */
@HiltAndroidApp
class FilmExposureApp : Application()
