package com.filmexposure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.filmexposure.ui.root.FilmExposureRoot
import com.filmexposure.ui.theme.FilmExposureTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.platform.LocalLifecycleOwner as ComposeUiLocalLifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner as LifecycleComposeLocalLifecycleOwner

/**
 * Точка входа приложения. Реальный контент — в FilmExposureRoot (Navigation Compose):
 * калибровка → основной экран. Разрешение камеры запрашивается на экране,
 * которому оно реально нужно (замер по превью), а не здесь.
 *
 * CompositionLocalProvider ниже — задокументированный обход известной несовместимости
 * lifecycle-runtime-compose 2.8.0 (свой androidx.lifecycle.compose.LocalLifecycleOwner)
 * с Compose UI ~1.6.x из BOM 2024.06.00 (версии зафиксированы в ТЗ): без этого моста
 * collectAsStateWithLifecycle() падает с "CompositionLocal LocalLifecycleOwner not present"
 * на первой же композиции. См. https://issuetracker.google.com (lifecycle 2.8.0 regression).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LifecycleComposeLocalLifecycleOwner provides ComposeUiLocalLifecycleOwner.current,
            ) {
                FilmExposureTheme {
                    FilmExposureRoot()
                }
            }
        }
    }
}
