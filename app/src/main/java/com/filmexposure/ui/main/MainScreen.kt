package com.filmexposure.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filmexposure.domain.model.ButtonSide
import com.filmexposure.ui.camera.CameraPermissionGate
import com.filmexposure.ui.camera.CameraPreview
import com.filmexposure.ui.camera.rememberCameraController
import com.filmexposure.ui.main.components.BwToggle
import com.filmexposure.ui.main.components.DistanceScale
import com.filmexposure.ui.main.components.ExposureDial
import com.filmexposure.ui.main.components.FixSceneButton
import com.filmexposure.ui.theme.glassPanel

/**
 * Главный экран-экспонометр — целиком переписан под модель "Профиль" и непрерывный живой замер
 * (решение по проекту, см. обсуждение упрощения). Живой поток с камеры анализируется постоянно
 * (LuminanceAnalyzer), MainViewModel.onLiveFrame пересчитывает Ev не чаще раза в ~200мс;
 * "Зафиксировать" стопорит кадр+Ev+пары одновременно.
 */
@Composable
fun MainScreen(onOpenMenu: () -> Unit, viewModel: MainViewModel = hiltViewModel()) {
    CameraPermissionGate {
        val controller = rememberCameraController()
        val settings by viewModel.settings.collectAsStateWithLifecycle()
        val activeProfile by viewModel.activeProfile.collectAsStateWithLifecycle()
        val isFrozen by viewModel.isFrozen.collectAsStateWithLifecycle()
        val validPairs by viewModel.validPairs.collectAsStateWithLifecycle()
        val selectedPairIndex by viewModel.selectedPairIndex.collectAsStateWithLifecycle()
        val focusDistanceM by viewModel.focusDistanceM.collectAsStateWithLifecycle()
        val dof by viewModel.dof.collectAsStateWithLifecycle()

        val frame by controller.frame.collectAsStateWithLifecycle()
        LaunchedEffect(frame) { frame?.let { viewModel.onLiveFrame(it) } }

        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(controller, isBW = settings.isBW, modifier = Modifier.fillMaxSize())

            // Верхняя панель — прозрачная, поверх живого кадра.
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val bwToggle: @Composable () -> Unit = { BwToggle(settings.isBW, viewModel::toggleBW) }
                val menuButton: @Composable () -> Unit = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Меню", tint = Color.White)
                    }
                }
                if (settings.pauseButtonSide == ButtonSide.LEFT) {
                    menuButton(); bwToggle()
                } else {
                    bwToggle(); menuButton()
                }
            }

            if (activeProfile == null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.glassPanel().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Профиль не выбран", color = Color.White)
                        Text(
                            "Создайте профиль в меню — без него не из чего считать пары",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            // ГРИП-панель — справа (или слева при pauseButtonSide=LEFT).
            DistanceScale(
                distanceM = focusDistanceM,
                onDistanceChange = viewModel::setFocusDistanceM,
                dof = dof,
                unit = settings.distanceUnit,
                modifier = Modifier
                    .align(if (settings.pauseButtonSide == ButtonSide.LEFT) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(12.dp)
                    .width(76.dp)
                    .fillMaxHeight(0.55f),
            )

            // Нижняя панель — катушки пар + кнопка фиксации.
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ExposureDial(
                    pairs = validPairs,
                    selectedIndex = selectedPairIndex,
                    onSelect = viewModel::selectPairIndex,
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                        .glassPanel()
                        .padding(vertical = 4.dp),
                )
                FixSceneButton(
                    isFrozen = isFrozen,
                    onToggle = {
                        val nowFrozen = viewModel.toggleFreeze()
                        if (nowFrozen) controller.freeze() else controller.unfreeze()
                    },
                )
            }
        }
    }
}
