package com.filmexposure.ui.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.filmexposure.ui.calibration.CalibrationScreen
import com.filmexposure.ui.main.MainScreen

private const val ROUTE_CALIBRATION = "calibration"
private const val ROUTE_MAIN = "main"

/**
 * Точка входа в UI. Пока calibrationConstant в настройках не задан — калибровка блокирует
 * основной экран (решение по проекту, см. обсуждение §4.2). Старт-дестинация выбирается один
 * раз после загрузки настроек, дальше пользователь идёт по обычному NavHost-стеку.
 */
@Composable
fun FilmExposureRoot(rootViewModel: RootViewModel = hiltViewModel()) {
    val isCalibrated by rootViewModel.isCalibrated.collectAsStateWithLifecycle()

    when (isCalibrated) {
        null -> LoadingScreen()
        else -> {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = if (isCalibrated == true) ROUTE_MAIN else ROUTE_CALIBRATION,
            ) {
                composable(ROUTE_CALIBRATION) {
                    CalibrationScreen(
                        onCalibrated = {
                            navController.navigate(ROUTE_MAIN) {
                                popUpTo(ROUTE_CALIBRATION) { inclusive = true }
                            }
                        },
                    )
                }
                composable(ROUTE_MAIN) { MainScreen() }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
