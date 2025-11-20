package digital.tonima.buracoff.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import digital.tonima.buracoff.R

@Composable
fun ControlButtons(
    isServiceRunning: Boolean,
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onStartService,
            enabled = !isServiceRunning,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
        ) {
            Text(stringResource(R.string.button_start_monitoring))
        }

        Button(
            onClick = onStopService,
            enabled = isServiceRunning,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3700B3))
        ) {
            Text(stringResource(R.string.button_stop))
        }
    }
}

