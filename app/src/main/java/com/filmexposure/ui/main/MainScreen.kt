package com.filmexposure.ui.main
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.filmexposure.domain.model.FocusDistanceStops
import com.filmexposure.ui.camera.CameraPermissionGate
import com.filmexposure.ui.camera.CameraPreview
import com.filmexposure.ui.camera.rememberCameraController
import com.filmexposure.ui.main.components.BwToggle
import com.filmexposure.ui.main.components.DofResultText
import com.filmexposure.ui.main.components.ExposureDial
import com.filmexposure.ui.main.components.FixSceneButton
import com.filmexposure.ui.main.components.FocusDistanceSlider
import com.filmexposure.ui.main.components.IsoSelector
import com.filmexposure.ui.theme.glassPanel
/**
 * Главный экран-экспонометр. Живой поток с камеры анализируется постоянно (LuminanceAnalyzer),
 * MainViewModel.onLiveFrame пересчитывает Ev не чаще раза в ~200мс; "Зафиксировать" стопорит
 * кадр+Ev+пары одновременно.
 *
 * ГРИП (решение по проекту, отменяет старую боковую панель): считается и показывается текстом по
 * центру ТОЛЬКО когда пользователь тапнул конкретную пару в ExposureDial; прокрутка/просмотр
 * пар сама по себе ГРИП не запускает. Дистанция фокусировки — отдельный линейный слайдер с
 * динамическим шагом (FocusDistanceStops), работает независимо от выбора пары.
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
        val focusDistanceIndex by viewModel.focusDistanceIndex.collectAsStateWithLifecycle()
        val dof by viewModel.dof.collectAsStateWithLifecycle()
        val frame by controller.frame.collectAsStateWithLifecycle()
        LaunchedEffect(frame) { frame?.let { viewModel.onLiveFrame(it) } }
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(controller, isBW = settings.isBW, modifier = Modifier.fillMaxSize())
            // Верхняя панель — прозрачная, поверх живого кадра. ISO — под иконкой ЧБ (решение по проекту).
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val bwAndIso: @Composable () -> Unit = {
                    Column(horizontalAlignment = Alignment.Start) {
                        BwToggle(settings.isBW, viewModel::toggleBW)
                        IsoSelector(currentIso = settings.filmIso, onIsoSelected = viewModel::setFilmIso)
                    }
                }
                val menuButton: @Composable () -> Unit = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Меню", tint = Color.White)
                    }
                }
                if (settings.pauseButtonSide == ButtonSide.LEFT) {
                    menuButton(); bwAndIso()
                } else {
                    bwAndIso(); menuButton()
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
            // Текст ГРИП — по центру экрана, только когда пара выбрана явным тапом.
            dof?.let {
                DofResultText(
                    dof = it,
                    unit = settings.distanceUnit,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                )
            }
            // Нижний блок — слайдер дистанции, катушки пар, кнопка фиксации.
            Column(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FocusDistanceSlider(
                    stops = FocusDistanceStops.METERS,
                    selectedIndex = focusDistanceIndex,
                    onIndexChanged = viewModel::setFocusDistanceIndex,
                    unit = settings.distanceUnit,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
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
}
