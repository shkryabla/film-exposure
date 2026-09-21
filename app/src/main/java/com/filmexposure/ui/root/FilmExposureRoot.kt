package com.filmexposure.ui.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.filmexposure.ui.calibration.CalibrationScreen
import com.filmexposure.ui.main.MainScreen
import com.filmexposure.ui.menu.MenuScreen
import com.filmexposure.ui.menu.about.AboutScreen
import com.filmexposure.ui.menu.catalog.CameraListScreen
import com.filmexposure.ui.menu.catalog.CustomCameraScreen
import com.filmexposure.ui.menu.catalog.CustomFilmScreen
import com.filmexposure.ui.menu.catalog.CustomLensScreen
import com.filmexposure.ui.menu.catalog.FilmListScreen
import com.filmexposure.ui.menu.catalog.LensListScreen
import com.filmexposure.ui.menu.rigs.RigEditorScreen
import com.filmexposure.ui.menu.rigs.RigListScreen
import com.filmexposure.ui.menu.settings.SettingsScreen

private const val ROUTE_MAIN = "main"
private const val ROUTE_MENU = "menu"
private const val ROUTE_CALIBRATION = "calibration"
private const val ROUTE_RIGS = "menu/rigs"
private const val ROUTE_RIG_EDITOR = "menu/rigs/editor"
private const val ARG_RIG_ID = "rigId"
private const val ROUTE_CAMERAS = "menu/cameras"
private const val ROUTE_CAMERA_NEW = "menu/cameras/new"
private const val ROUTE_LENSES = "menu/lenses"
private const val ROUTE_LENS_NEW = "menu/lenses/new"
private const val ROUTE_FILMS = "menu/films"
private const val ROUTE_FILM_NEW = "menu/films/new"
private const val ROUTE_SETTINGS = "menu/settings"
private const val ROUTE_ABOUT = "menu/about"

/** Точка входа в UI — полный граф навигации §6.1–§6.4. */
@Composable
fun FilmExposureRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_MAIN) {
        composable(ROUTE_MAIN) {
            MainScreen(onOpenMenu = { navController.navigate(ROUTE_MENU) })
        }

        composable(ROUTE_MENU) {
            MenuScreen(
                onBack = { navController.popBackStack() },
                onOpenRigs = { navController.navigate(ROUTE_RIGS) },
                onOpenCameras = { navController.navigate(ROUTE_CAMERAS) },
                onOpenLenses = { navController.navigate(ROUTE_LENSES) },
                onOpenFilms = { navController.navigate(ROUTE_FILMS) },
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
                onOpenAbout = { navController.navigate(ROUTE_ABOUT) },
            )
        }

        composable(ROUTE_CALIBRATION) {
            CalibrationScreen(onCalibrated = { navController.popBackStack() })
        }

        composable(ROUTE_RIGS) {
            RigListScreen(
                onBack = { navController.popBackStack() },
                onCreateRig = { navController.navigate("$ROUTE_RIG_EDITOR?$ARG_RIG_ID=-1") },
                onEditRig = { id -> navController.navigate("$ROUTE_RIG_EDITOR?$ARG_RIG_ID=$id") },
            )
        }
        composable(
            route = "$ROUTE_RIG_EDITOR?$ARG_RIG_ID={$ARG_RIG_ID}",
            arguments = listOf(navArgument(ARG_RIG_ID) { type = NavType.LongType; defaultValue = -1L }),
        ) { entry ->
            val rigId = entry.arguments?.getLong(ARG_RIG_ID)?.takeIf { it >= 0L }
            RigEditorScreen(rigId = rigId, onBack = { navController.popBackStack() })
        }

        composable(ROUTE_CAMERAS) {
            CameraListScreen(
                onBack = { navController.popBackStack() },
                onAddCustom = { navController.navigate(ROUTE_CAMERA_NEW) },
            )
        }
        composable(ROUTE_CAMERA_NEW) { CustomCameraScreen(onBack = { navController.popBackStack() }) }

        composable(ROUTE_LENSES) {
            LensListScreen(
                onBack = { navController.popBackStack() },
                onAddCustom = { navController.navigate(ROUTE_LENS_NEW) },
            )
        }
        composable(ROUTE_LENS_NEW) { CustomLensScreen(onBack = { navController.popBackStack() }) }

        composable(ROUTE_FILMS) {
            FilmListScreen(
                onBack = { navController.popBackStack() },
                onAddCustom = { navController.navigate(ROUTE_FILM_NEW) },
            )
        }
        composable(ROUTE_FILM_NEW) { CustomFilmScreen(onBack = { navController.popBackStack() }) }

        composable(ROUTE_SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenCalibration = { navController.navigate(ROUTE_CALIBRATION) },
            )
        }

        composable(ROUTE_ABOUT) { AboutScreen(onBack = { navController.popBackStack() }) }
    }
}
