package com.filmexposure.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.filmexposure.domain.model.AppSettings
import com.filmexposure.domain.model.ButtonSide
import com.filmexposure.domain.model.DEFAULT_CALIBRATION_CONSTANT
import com.filmexposure.domain.model.DistanceUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "settings")

private object Keys {
    val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
    val PAUSE_BUTTON_SIDE = stringPreferencesKey("pause_button_side")
    val IS_BW = booleanPreferencesKey("is_bw")
    val CALIBRATION_CONSTANT = floatPreferencesKey("calibration_constant")
    val IS_CALIBRATED = booleanPreferencesKey("is_calibrated")
    val SELECTED_PROFILE_ID = longPreferencesKey("selected_profile_id")
}

private fun readSettings(prefs: Preferences): AppSettings = AppSettings(
    distanceUnit = prefs[Keys.DISTANCE_UNIT]?.let { DistanceUnit.valueOf(it) } ?: DistanceUnit.METERS,
    pauseButtonSide = prefs[Keys.PAUSE_BUTTON_SIDE]?.let { ButtonSide.valueOf(it) } ?: ButtonSide.RIGHT,
    isBW = prefs[Keys.IS_BW] ?: false,
    calibrationConstant = prefs[Keys.CALIBRATION_CONSTANT] ?: DEFAULT_CALIBRATION_CONSTANT,
    isCalibrated = prefs[Keys.IS_CALIBRATED] ?: false,
    selectedProfileId = prefs[Keys.SELECTED_PROFILE_ID],
)

/**
 * Первый запуск: isBW = false, calibrationConstant = DEFAULT_CALIBRATION_CONSTANT,
 * isCalibrated = false. Калибровка необязательна — экран доступен из настроек, но ничего
 * не блокирует.
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
            prefs[Keys.PAUSE_BUTTON_SIDE] = updated.pauseButtonSide.name
            prefs[Keys.IS_BW] = updated.isBW
            prefs[Keys.CALIBRATION_CONSTANT] = updated.calibrationConstant
            prefs[Keys.IS_CALIBRATED] = updated.isCalibrated
            if (updated.selectedProfileId != null) {
                prefs[Keys.SELECTED_PROFILE_ID] = updated.selectedProfileId
            } else {
                prefs.remove(Keys.SELECTED_PROFILE_ID)
            }
        }
    }
}
