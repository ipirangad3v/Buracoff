package digital.tonima.buracoff.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import digital.tonima.buracoff.R
import digital.tonima.buracoff.presentation.components.ControlButtons
import digital.tonima.buracoff.presentation.components.GraphCard
import digital.tonima.buracoff.presentation.components.StatusCard

@Composable
fun PotholeScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onStartService: () -> Unit,
    onStopService: () -> Unit
) {
    val lastImpact by viewModel.lastImpact.collectAsState()
    val count by viewModel.potholeCount.collectAsState()
    val sensorHistory by viewModel.sensorHistory.collectAsState()
    var isServiceRunning by remember { mutableStateOf(false) }

    val isPotholeDetected = sensorHistory.lastOrNull()?.isPothole ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.title_pothole_detector),
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        StatusCard(
            isPotholeDetected = isPotholeDetected,
            lastImpact = lastImpact
        )

        Spacer(modifier = Modifier.height(20.dp))

        GraphCard(sensorHistory = sensorHistory)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.label_total_detected, count),
            color = Color.Gray,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(50.dp))

        ControlButtons(
            isServiceRunning = isServiceRunning,
            onStartService = {
                isServiceRunning = true
                onStartService()
            },
            onStopService = {
                isServiceRunning = false
                onStopService()
            }
        )
    }
}

