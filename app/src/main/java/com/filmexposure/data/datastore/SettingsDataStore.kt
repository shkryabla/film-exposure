package com.filmexposure.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.filmexposure.domain.model.AppSettings
import com.filmexposure.domain.model.ButtonSide
import com.filmexposure.domain.model.DEFAULT_CALIBRATION_CONSTANT
import com.filmexposure.domain.model.DistanceUnit
import com.filmexposure.domain.model.ScaleMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "settings")

private object Keys {
    val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
    val METERING_POINTS = intPreferencesKey("metering_points")
    val SCALE_MODE = stringPreferencesKey("scale_mode")
    val SHOW_RULE_OF_THIRDS = booleanPreferencesKey("show_rule_of_thirds")
    val SHOW_INTERSECTIONS = booleanPreferencesKey("show_intersections")
    val SHOW_ZONE_OVERLAY = booleanPreferencesKey("show_zone_overlay")
    val PAUSE_BUTTON_SIDE = stringPreferencesKey("pause_button_side")
    val IS_BW = booleanPreferencesKey("is_bw")
    val CALIBRATION_CONSTANT = floatPreferencesKey("calibration_constant")
    val IS_CALIBRATED = booleanPreferencesKey("is_calibrated")
    val SELECTED_RIG_ID = androidx.datastore.preferences.core.longPreferencesKey("selected_rig_id")
    val SELECTED_FILM_ID = stringPreferencesKey("selected_film_id")
}

private fun readSettings(prefs: Preferences): AppSettings = AppSettings(
    distanceUnit = prefs[Keys.DISTANCE_UNIT]?.let { DistanceUnit.valueOf(it) } ?: DistanceUnit.METERS,
    meteringPoints = prefs[Keys.METERING_POINTS] ?: 3,
    scaleMode = prefs[Keys.SCALE_MODE]?.let { ScaleMode.valueOf(it) } ?: ScaleMode.SIMPLE,
    showRuleOfThirds = prefs[Keys.SHOW_RULE_OF_THIRDS] ?: true,
    showIntersections = prefs[Keys.SHOW_INTERSECTIONS] ?: false,
    showZoneOverlay = prefs[Keys.SHOW_ZONE_OVERLAY] ?: false,
    pauseButtonSide = prefs[Keys.PAUSE_BUTTON_SIDE]?.let { ButtonSide.valueOf(it) } ?: ButtonSide.RIGHT,
    isBW = prefs[Keys.IS_BW] ?: false,
    calibrationConstant = prefs[Keys.CALIBRATION_CONSTANT] ?: DEFAULT_CALIBRATION_CONSTANT,
    isCalibrated = prefs[Keys.IS_CALIBRATED] ?: false,
    selectedRigId = prefs[Keys.SELECTED_RIG_ID],
    selectedFilmId = prefs[Keys.SELECTED_FILM_ID],
)

/**
 * Первый запуск (ТЗ §5.3): isBW = false, calibrationConstant = DEFAULT_CALIBRATION_CONSTANT,
 * isCalibrated = false. Калибровка необязательна — приложение сразу работоспособно (решение
 * по проекту): экран калибровки доступен из настроек, но ничего не блокирует.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val settings: Flow<AppSettings> = context.dataStore.data.map(::readSettings)

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val updated = transform(readSettings(prefs))
            prefs[Keys.DISTANCE_UNIT] = updated.distanceUnit.name
            prefs[Keys.METERING_POINTS] = updated.meteringPoints
            prefs[Keys.SCALE_MODE] = updated.scaleMode.name
            prefs[Keys.SHOW_RULE_OF_THIRDS] = updated.showRuleOfThirds
            prefs[Keys.SHOW_INTERSECTIONS] = updated.showIntersections
            prefs[Keys.SHOW_ZONE_OVERLAY] = updated.showZoneOverlay
            prefs[Keys.PAUSE_BUTTON_SIDE] = updated.pauseButtonSide.name
            prefs[Keys.IS_BW] = updated.isBW
            prefs[Keys.CALIBRATION_CONSTANT] = updated.calibrationConstant
            prefs[Keys.IS_CALIBRATED] = updated.isCalibrated
            if (updated.selectedRigId != null) {
                prefs[Keys.SELECTED_RIG_ID] = updated.selectedRigId
            } else {
                prefs.remove(Keys.SELECTED_RIG_ID)
            }
            if (updated.selectedFilmId != null) {
                prefs[Keys.SELECTED_FILM_ID] = updated.selectedFilmId
            } else {
                prefs.remove(Keys.SELECTED_FILM_ID)
            }
        }
    }
}
