package com.filmexposure.ui.camera

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Единственное место в приложении, где запрашивается CAMERA-разрешение (ТЗ §17 — вход в
 * приложение не должен требовать разрешений заранее; запрашиваем только там, где оно реально
 * нужно — калибровка и главный экран замера).
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionGate(content: @Composable () -> Unit) {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (permissionState.status !is PermissionStatus.Granted) {
            permissionState.launchPermissionRequest()
        }
    }

    when (val status = permissionState.status) {
        is PermissionStatus.Granted -> content()
        is PermissionStatus.Denied -> DeniedContent(
            shouldShowRationale = status.shouldShowRationale,
            onRequest = { permissionState.launchPermissionRequest() },
        )
    }
}

@Composable
private fun DeniedContent(shouldShowRationale: Boolean, onRequest: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (shouldShowRationale) {
                    "Без доступа к камере замер экспозиции невозможен — приложение считает " +
                        "яркость сцены по кадру превью."
                } else {
                    "Нужен доступ к камере для замера экспозиции."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Button(onClick = onRequest, modifier = Modifier.padding(top = 16.dp)) {
                Text("Предоставить доступ")
            }
        }
    }
}
