package com.filmexposure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.filmexposure.ui.root.FilmExposureRoot
import com.filmexposure.ui.theme.FilmExposureTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Точка входа приложения. Реальный контент — в FilmExposureRoot (Navigation Compose):
 * калибровка → основной экран. Разрешение камеры запрашивается на экране,
 * которому оно реально нужно (замер по превью), а не здесь.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilmExposureTheme {
                FilmExposureRoot()
            }
        }
    }
}
