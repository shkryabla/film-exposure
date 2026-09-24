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
import com.filmexposure.ui.menu.profiles.ProfileEditorScreen
import com.filmexposure.ui.menu.profiles.ProfileListScreen
import com.filmexposure.ui.menu.settings.SettingsScreen

private const val ROUTE_MAIN = "main"
private const val ROUTE_MENU = "menu"
private const val ROUTE_CALIBRATION = "calibration"
private const val ROUTE_PROFILES = "menu/profiles"
private const val ROUTE_PROFILE_EDITOR = "menu/profiles/editor"
private const val ARG_PROFILE_ID = "profileId"
private const val ROUTE_SETTINGS = "menu/settings"
private const val ROUTE_ABOUT = "menu/about"

/** Точка входа в UI — граф навигации по модели "Профиль" (§6.2 упрощено до Профили/Настройки/О программе). */
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
                onOpenProfiles = { navController.navigate(ROUTE_PROFILES) },
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
                onOpenAbout = { navController.navigate(ROUTE_ABOUT) },
            )
        }

        composable(ROUTE_CALIBRATION) {
            CalibrationScreen(onCalibrated = { navController.popBackStack() })
        }

        composable(ROUTE_PROFILES) {
            ProfileListScreen(
                onBack = { navController.popBackStack() },
                onCreateProfile = { navController.navigate("$ROUTE_PROFILE_EDITOR?$ARG_PROFILE_ID=-1") },
                onEditProfile = { id -> navController.navigate("$ROUTE_PROFILE_EDITOR?$ARG_PROFILE_ID=$id") },
            )
        }
        composable(
            route = "$ROUTE_PROFILE_EDITOR?$ARG_PROFILE_ID={$ARG_PROFILE_ID}",
            arguments = listOf(navArgument(ARG_PROFILE_ID) { type = NavType.LongType; defaultValue = -1L }),
        ) { entry ->
            val profileId = entry.arguments?.getLong(ARG_PROFILE_ID)?.takeIf { it >= 0L }
            ProfileEditorScreen(profileId = profileId, onBack = { navController.popBackStack() })
        }

        composable(ROUTE_SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenCalibration = { navController.navigate(ROUTE_CALIBRATION) },
            )
        }

        composable(ROUTE_ABOUT) { AboutScreen(onBack = { navController.popBackStack() }) }
    }
}
